package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.ShortLinkUseCase;
import com.restaurante.bot.dto.GenerateLinkIn;
import com.restaurante.bot.dto.GenerateTokenRequestDTO;
import com.restaurante.bot.dto.GenerateTokenResponseDTO;
import com.restaurante.bot.dto.SessionValidationRequestDTO;
import com.restaurante.bot.dto.SessionValidationResponseDTO;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.security.SessionRegistryService;
import com.restaurante.bot.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/${app.request.mapping}/security")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@Slf4j
public class SecurityController {

    private final JwtUtil jwtUtil;

    private final CompanyRepository companyRepository;

    private final ShortLinkUseCase shortLinkservice;

    private final SessionRegistryService sessionRegistryService;

    @Value("${landing.page.url}")
    private String landingPageUrl;

    @Value("${backend.url}")
    private String backendUrl;

    @Value("${app.request.mapping}")
    private String mappingPageUrl;

    @PostMapping("/generateToken")
    public ResponseEntity<?> generateToken(@Valid @RequestBody GenerateTokenRequestDTO generateTokenRequestDTO) {
        log.info("Se inicia el endpoint que genera un token");
        if (!companyRepository.existsByExternalCompanyId(generateTokenRequestDTO.getCompanyId())) {
            return new ResponseEntity<>("Compañía no existe", HttpStatus.NOT_FOUND);
        }
        // Validate apiKey provided in the request
        Company company = companyRepository.findByExternalCompanyId(generateTokenRequestDTO.getCompanyId());
        if (company.getApiKey() == null || !company.getApiKey().equals(generateTokenRequestDTO.getApiKey())) {
            return new ResponseEntity<>("apiKey invalido", HttpStatus.UNAUTHORIZED);
        }

        String sessionId = jwtUtil.generateSessionId();
        String token = jwtUtil.generateToken(
            generateTokenRequestDTO.getCompanyId(),
            generateTokenRequestDTO.getUserId(),
            sessionId);
        sessionRegistryService.registerSession(
            sessionId,
            generateTokenRequestDTO.getCompanyId(),
            generateTokenRequestDTO.getUserId());
        return new ResponseEntity<>(
                GenerateTokenResponseDTO.builder()
                .token(token)
                        .sessionId(sessionId)
                        .build(),
                HttpStatus.OK);

    }

    @PostMapping("/generateLink")
    public ResponseEntity<String> generateLink(@Valid @RequestBody GenerateLinkIn generateLinkIn) {
        Map<String, String> queryParams = new HashMap<>();

        if (!companyRepository.existsByExternalCompanyId(generateLinkIn.getCompanyId())) {
            return new ResponseEntity<>("Compañía no existe", HttpStatus.NOT_FOUND);
        }

        Company company= companyRepository.findByExternalCompanyId(generateLinkIn.getCompanyId());

        // Validate apiKey provided in the request
        if (generateLinkIn.getApiKey() == null || !generateLinkIn.getApiKey().equals(company.getApiKey())) {
            return new ResponseEntity<>("apiKey invalido", HttpStatus.UNAUTHORIZED);
        }

        String sessionId = generateLinkIn.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            log.warn("generateLink: sessionId absent in request, generating a new one");
            sessionId = jwtUtil.generateSessionId();
        }

        String token = jwtUtil.generateToken(generateLinkIn.getCompanyId(), generateLinkIn.getUserId(), sessionId);
        sessionRegistryService.registerSession(sessionId, generateLinkIn.getCompanyId(), generateLinkIn.getUserId());

        queryParams.put("token", token);
        queryParams.put("session_id", sessionId);
        queryParams.put("companyId", String.valueOf(generateLinkIn.getCompanyId()));
        // Note: do NOT include apiKey in the generated URL; it's used only for validation
        queryParams.put("mesa", generateLinkIn.getMesa());
        queryParams.put("userToken", generateLinkIn.getUserToken());

        if (generateLinkIn.getQr() != null && !generateLinkIn.getQr().isEmpty()) {
            queryParams.put("qr", generateLinkIn.getQr());
        }

        if (generateLinkIn.getDelivery() != null && !generateLinkIn.getDelivery().isEmpty()) {
            queryParams.put("delivery", generateLinkIn.getDelivery());
        }

        if (company.getLandingTemplate() != null && !company.getLandingTemplate().isEmpty()) {
            queryParams.put("templateLanding", company.getLandingTemplate());
        }

        String fullLink = buildUrl(landingPageUrl, queryParams);
        var shortLink = shortLinkservice.createShortLink(fullLink);
        String shortUrl = backendUrl + "/" + mappingPageUrl + "/h/" + shortLink.getShortCode();
        return new ResponseEntity<>(shortUrl, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(Map.of("message", "Authorization header invalido"), HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return new ResponseEntity<>(Map.of("message", "Token invalido"), HttpStatus.UNAUTHORIZED);
        }

        String sessionId = jwtUtil.extractSessionId(token);
        sessionRegistryService.invalidateSession(sessionId);
        return ResponseEntity.ok(Map.of(
                "message", "Sesion cerrada correctamente",
                "session_id", sessionId));
    }

    @PostMapping("/validateSession")
    public ResponseEntity<SessionValidationResponseDTO> validateSession(
            @RequestBody SessionValidationRequestDTO requestDTO) {
        SessionRegistryService.SessionStatus sessionStatus = sessionRegistryService.getSessionStatus(requestDTO.getSessionId());
        return ResponseEntity.ok(SessionValidationResponseDTO.builder()
                .sessionId(sessionStatus.sessionId())
                .active(sessionStatus.active())
                .expired(sessionStatus.expired())
            .remainingMs(sessionStatus.remainingMs())
                .expiresAt(sessionStatus.expiresAt())
                .build());
    }

    private String buildUrl(String baseUrl, Map<String, String> params) {
        StringBuilder url = new StringBuilder(baseUrl);

        if (params != null && !params.isEmpty()) {
            url.append("?");
            params.forEach((key, value) -> {
                if (url.charAt(url.length() - 1) != '?') {
                    url.append("&");
                }
                url.append(key);
                url.append("=");
                // Si es el token, no lo encodees
                if (key.equals("token")) {
                    url.append(value);
                } else {
                    url.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
            });
        }

        return url.toString();
    }
}
