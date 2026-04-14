package com.restaurante.bot.application.services;

import com.restaurante.bot.application.ports.incoming.SecurityUseCase;
import com.restaurante.bot.application.ports.incoming.ShortLinkUseCase;
import com.restaurante.bot.dto.GenerateLinkIn;
import com.restaurante.bot.dto.GenerateLinkResponseDTO;
import com.restaurante.bot.dto.GenerateTokenRequestDTO;
import com.restaurante.bot.dto.GenerateTokenResponseDTO;
import com.restaurante.bot.dto.SessionValidationRequestDTO;
import com.restaurante.bot.dto.SessionValidationResponseDTO;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.security.SessionRegistryService;
import com.restaurante.bot.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Security Application Service - contains all authentication and token management logic.
 * 
 * This service implements the SecurityUseCase interface and is responsible for:
 * - Token generation and validation
 * - Session management
 * - API key validation
 * - Link generation for QR codes and other integrations
 * 
 * Clean Architecture Pattern:
 * - Controllers delegate security operations to this service via SecurityUseCase interface
 * - This service contains the actual business logic (was previously in SecurityController)
 * - Breaks dependency from presentation layer to security logic
 * - Makes security logic testable and reusable
 */
@Service("securityApplicationService")
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class SecurityApplicationService implements SecurityUseCase {
    
    private final JwtUtil jwtUtil;
    private final CompanyRepository companyRepository;
    private final ShortLinkUseCase shortLinkService;
    private final SessionRegistryService sessionRegistryService;
    
    @Value("${landing.page.url}")
    private String landingPageUrl;
    
    @Value("${backend.url}")
    private String backendUrl;
    
    @Value("${app.request.mapping}")
    private String mappingPageUrl;
    
    /**
     * Generate JWT token for API access
     * 
     * Performs:
     * 1. Validates company exists by externalCompanyId
     * 2. Checks for duplicate externalCompanyIds (data integrity)
     * 3. Retrieves company and validates API key
     * 4. Generates unique session ID
     * 5. Creates JWT token with company and user info
     * 6. Registers session in SessionRegistry
     * 
     * @param generateTokenRequestDTO contains externalCompanyId, userId, apiKey
     * @return GenerateTokenResponseDTO with token and sessionId
     * @throws DomainException if validation fails
     */
    @Override
    @Transactional
    public GenerateTokenResponseDTO generateToken(GenerateTokenRequestDTO generateTokenRequestDTO) {
        log.info("Generating token for company ID: {}", generateTokenRequestDTO.getExternalCompanyId());
        
        Long externalCompanyId = generateTokenRequestDTO.getExternalCompanyId();
        Company company = validateAndRetrieveCompany(externalCompanyId);
        validateApiKey(company, generateTokenRequestDTO.getApiKey());
        
        String sessionId = jwtUtil.generateSessionId();
        String token = jwtUtil.generateToken(
            externalCompanyId,
            generateTokenRequestDTO.getUserId(),
            sessionId
        );
        
        sessionRegistryService.registerSession(
            sessionId,
            externalCompanyId,
            generateTokenRequestDTO.getUserId()
        );
        
        log.info("Token generated successfully for company ID: {}", externalCompanyId);
        
        return GenerateTokenResponseDTO.builder()
                .token(token)
                .sessionId(sessionId)
                .build();
    }
    
    /**
     * Generate secure short link for landing page access
     * 
     * Performs:
     * 1. Validates company and API key
     * 2. Ensures sessionId is present (generates if needed)
     * 3. Creates JWT token with session
     * 4. Registers session
     * 5. Builds full URL with all parameters
     * 6. Creates short link via ShortLinkUseCase
     * 7. Returns both short and full URLs for client usage
     * 
     * @param generateLinkIn contains company, mesa, delivery, QR info
     * @return GenerateLinkResponseDTO with shortUrl, fullUrl, token, sessionId
     * @throws DomainException if company invalid or other validation failures
     */
    @Override
    @Transactional
    public GenerateLinkResponseDTO generateLink(GenerateLinkIn generateLinkIn) {
        log.info("Generating link for company ID: {}", generateLinkIn.getExternalCompanyId());
        
        Long externalCompanyId = generateLinkIn.getExternalCompanyId();
        Company company = validateAndRetrieveCompany(externalCompanyId);
        validateApiKey(company, generateLinkIn.getApiKey());
        
        String sessionId = generateLinkIn.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            log.debug("No sessionId provided, generating new one");
            sessionId = jwtUtil.generateSessionId();
        }
        
        String token = jwtUtil.generateToken(
            externalCompanyId,
            generateLinkIn.getUserId(),
            sessionId
        );
        
        sessionRegistryService.registerSession(
            sessionId,
            externalCompanyId,
            generateLinkIn.getUserId()
        );
        
        Map<String, String> queryParams = buildQueryParameters(generateLinkIn, company, token, sessionId);
        String fullLink = buildUrl(landingPageUrl, queryParams);
        
        var shortLink = shortLinkService.createShortLink(fullLink);
        String shortUrl = backendUrl + "/" + mappingPageUrl + "/h/" + shortLink.getShortCode();
        
        log.info("Link generated successfully for company ID: {}", externalCompanyId);
        
        GenerateLinkResponseDTO response = GenerateLinkResponseDTO.builder()
            .shortUrl(shortUrl)
            .fullUrl(fullLink)
            .shortCode(shortLink.getShortCode())
            .token(token)
            .sessionId(sessionId)
            .build();
        
        return response;
    }
    
    /**
     * Logout user by invalidating their session
     * 
     * Performs:
     * 1. Validates Authorization header format
     * 2. Validates JWT token is active
     * 3. Extracts session ID from token
     * 4. Invalidates session in SessionRegistry
     * 
     * @param authorizationHeader Bearer token from Authorization header
     * @throws DomainException if header invalid or token invalid
     */
    @Override
    @Transactional
    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new DomainException(
                DomainErrorCode.UNAUTHORIZED,
                "Invalid authorization header format"
            );
        }
        
        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            throw new DomainException(
                DomainErrorCode.UNAUTHORIZED,
                "Token is invalid or expired"
            );
        }
        
        String sessionId = jwtUtil.extractSessionId(token);
        sessionRegistryService.invalidateSession(sessionId);
        
        log.info("Session invalidated: {}", sessionId);
    }
    
    /**
     * Validate if a session is still active
     * 
     * @param requestDTO contains sessionId to validate
     * @return SessionValidationResponseDTO with session status, expiration info
     */
    @Override
    @Transactional(readOnly = true)
    public SessionValidationResponseDTO validateSession(SessionValidationRequestDTO requestDTO) {
        SessionRegistryService.SessionStatus sessionStatus = 
            sessionRegistryService.getSessionStatus(requestDTO.getSessionId());
        
        return SessionValidationResponseDTO.builder()
                .sessionId(sessionStatus.sessionId())
                .active(sessionStatus.active())
                .expired(sessionStatus.expired())
                .remainingMs(sessionStatus.remainingMs())
                .expiresAt(sessionStatus.expiresAt())
                .build();
    }
    
    /**
     * PRIVATE HELPER METHODS
     */
    
    /**
     * Validates company exists and is unique by externalCompanyId
     * Throws DomainException if company not found or duplicated
     */
    private Company validateAndRetrieveCompany(Long externalCompanyId) {
        if (!companyRepository.existsByExternalCompanyId(externalCompanyId)) {
            throw new DomainException(
                DomainErrorCode.NOT_FOUND,
                "Company not found with external ID: " + externalCompanyId
            );
        }
        
        long matches = companyRepository.countByExternalCompanyId(externalCompanyId);
        if (matches > 1) {
            log.error("Duplicate externalCompanyId detected: {}", externalCompanyId);
            throw new DomainException(
                DomainErrorCode.CONFLICT,
                "Duplicate externalCompanyId detected - contact administrator"
            );
        }
        
        return companyRepository.findByExternalCompanyId(externalCompanyId);
    }
    
    /**
     * Validates API key matches the company's stored key
     * Throws DomainException if validation fails
     */
    private void validateApiKey(Company company, String providedApiKey) {
        if (company.getApiKey() == null || !company.getApiKey().equals(providedApiKey)) {
            log.warn("Invalid API key provided for company: {}", company.getId());
            throw new DomainException(
                DomainErrorCode.UNAUTHORIZED,
                "Invalid API key"
            );
        }
    }
    
    /**
     * Build query parameters map for URL generation
     * Includes conditional parameters based on request data and company settings
     */
    private Map<String, String> buildQueryParameters(
            GenerateLinkIn generateLinkIn,
            Company company,
            String token,
            String sessionId) {
        
        Map<String, String> queryParams = new HashMap<>();
        
        queryParams.put("token", token);
        queryParams.put("session_id", sessionId);
        queryParams.put("externalCompanyId", String.valueOf(generateLinkIn.getExternalCompanyId()));
        queryParams.put("mesa", generateLinkIn.getMesa());
        queryParams.put("userToken", generateLinkIn.getUserToken());
        queryParams.put("source_id", generateLinkIn.getSourceId());
        
        // Add optional parameters if present
        if (generateLinkIn.getQr() != null && !generateLinkIn.getQr().isEmpty()) {
            queryParams.put("qr", generateLinkIn.getQr());
        }
        
        if (generateLinkIn.getDelivery() != null && !generateLinkIn.getDelivery().isEmpty()) {
            queryParams.put("delivery", generateLinkIn.getDelivery());
        }
        
        // Add company's landing template if configured
        if (company.getLandingTemplate() != null && !company.getLandingTemplate().isEmpty()) {
            queryParams.put("templateLanding", company.getLandingTemplate());
        }
        
        // NOTE: apiKey is NOT included in URL for security reasons
        
        return queryParams;
    }
    
    /**
     * Builds full URL with query parameters
     * Encodes parameters to ensure URL safety
     */
    private String buildUrl(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        
        String query = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isBlank())
                .map(e -> {
                    String encodedValue = URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8);
                    return e.getKey() + "=" + encodedValue;
                })
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
        
        return query.isEmpty() ? baseUrl : baseUrl + "?" + query;
    }
}
