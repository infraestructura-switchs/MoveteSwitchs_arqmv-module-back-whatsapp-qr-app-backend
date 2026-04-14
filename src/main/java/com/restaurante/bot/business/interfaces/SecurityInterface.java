package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.GenerateLinkIn;
import com.restaurante.bot.dto.GenerateLinkResponseDTO;
import com.restaurante.bot.dto.GenerateTokenRequestDTO;
import com.restaurante.bot.dto.GenerateTokenResponseDTO;
import com.restaurante.bot.dto.SessionValidationRequestDTO;
import com.restaurante.bot.dto.SessionValidationResponseDTO;

/**
 * Interface for security operations - defines incoming port for authentication and token management.
 * This interface extracts security logic from the controller and defines the contract for
 * all authentication and token-related operations.
 * 
 * Clean Architecture Design:
 * - This is an "incoming port" - defines how external clients interact with security logic
 * - Implementation moves to SecurityApplicationService
 * - Controllers become thin adapters that simply delegate to this interface
 */
public interface SecurityInterface {
    
    /**
     * Generate JWT token for a company with API key validation.
     * 
     * Business Logic:
     * - Validates company exists by externalCompanyId
     * - Checks for duplicate externalCompanyIds
     * - Validates API key matches
     * - Creates session ID
     * - Generates JWT token
     * - Registers session in SessionRegistry
     *
     * @param generateTokenRequestDTO contains externalCompanyId, userId, and apiKey
     * @return GenerateTokenResponseDTO with token and sessionId
     * @throws DomainException if company not found, invalid apiKey, or duplicates detected
     */
    GenerateTokenResponseDTO generateToken(GenerateTokenRequestDTO generateTokenRequestDTO);
    
    /**
     * Generate a secure short link for accessing the landing page.
     *
     * Business Logic:
     * - Validates company exists
     * - Validates API key
     * - Generates or reuses session ID
     * - Creates JWT token
     * - Builds full URL with parameters
     * - Creates short link via ShortLinkUseCase
     * - Returns both short and full URLs
     *
     * @param generateLinkIn contains company details, mesa, delivery info, etc.
     * @return GenerateLinkResponseDTO with shortUrl, fullUrl, token, and sessionId
     * @throws DomainException if company not found, invalid apiKey, or duplicates detected
     */
    GenerateLinkResponseDTO generateLink(GenerateLinkIn generateLinkIn);
    
    /**
     * Logout a user by invalidating their session.
     * 
     * Business Logic:
     * - Validates Bearer token format
     * - Validates token is active
     * - Extracts session ID from token
     * - Invalidates session in SessionRegistry
     *
     * @param authorizationHeader Bearer token from Authorization header
     * @return response indicating successful logout and session ID
     * @throws DomainException if token is invalid or missing
     */
    void logout(String authorizationHeader);
    
    /**
     * Validate if a session is still active.
     *
     * Business Logic:
     * - Retrieves session status from SessionRegistry
     * - Returns session details (active, expired, remainingMs, expiresAt)
     *
     * @param sessionValidationRequestDTO contains sessionId to validate
     * @return SessionValidationResponseDTO with session status and details
     */
    SessionValidationResponseDTO validateSession(SessionValidationRequestDTO sessionValidationRequestDTO);
}
