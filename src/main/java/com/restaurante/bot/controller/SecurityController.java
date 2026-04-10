package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.SecurityUseCase;
import com.restaurante.bot.dto.GenerateLinkIn;
import com.restaurante.bot.dto.GenerateLinkResponseDTO;
import com.restaurante.bot.dto.GenerateTokenRequestDTO;
import com.restaurante.bot.dto.GenerateTokenResponseDTO;
import com.restaurante.bot.dto.SessionValidationRequestDTO;
import com.restaurante.bot.dto.SessionValidationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Security REST Controller - handles authentication and session management endpoints.
 * 
 * REFACTORED - Clean Architecture Pattern:
 * This controller is now a THIN ADAPTER that:
 * - Maps HTTP requests to DTOs
 * - Delegates to SecurityUseCase (business logic)
 * - Maps responses back to HTTP
 * - NO BUSINESS LOGIC remains in this class
 * 
 * All security logic (token generation, validation, session management) has been moved to:
 * - SecurityApplicationService implements SecurityUseCase
 * - Injected via dependency injection
 * - Called from controller endpoints
 * 
 * Benefits:
 * - Controllers are testable in isolation from HTTP concerns
 * - Business logic is reusable from any other adapter (CLI, MessageQueue, etc.)
 * - Clear separation of concerns
 * - Easy to mock for unit tests
 */
@RestController
@RequestMapping("/${app.request.mapping}/security")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@Slf4j
public class SecurityController {

    private final SecurityUseCase securityUseCase;

    /**
     * Generate JWT token endpoint
     * HTTP: POST /security/generateToken
     * Maps request to DTO → delegates to SecurityUseCase → returns response
     */
    @PostMapping("/generateToken")
    public ResponseEntity<GenerateTokenResponseDTO> generateToken(
            @Valid @RequestBody GenerateTokenRequestDTO generateTokenRequestDTO) {
        log.info("Token generation requested for company: {}", generateTokenRequestDTO.getExternalCompanyId());
        GenerateTokenResponseDTO response = securityUseCase.generateToken(generateTokenRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Generate short link endpoint
     * HTTP: POST /security/generateLink
     * Maps request to DTO → delegates to SecurityUseCase → returns response
     */
    @PostMapping("/generateLink")
    public ResponseEntity<GenerateLinkResponseDTO> generateLink(
            @Valid @RequestBody GenerateLinkIn generateLinkIn) {
        log.info("Link generation requested for company: {}", generateLinkIn.getExternalCompanyId());
        GenerateLinkResponseDTO response = securityUseCase.generateLink(generateLinkIn);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Logout endpoint
     * HTTP: POST /security/logout
     * Extracts Authorization header → delegates to SecurityUseCase
     * Returns logout confirmation message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        log.info("Logout requested");
        securityUseCase.logout(authorizationHeader);
        return ResponseEntity.ok(Map.of("message", "Session closed successfully"));
    }

    /**
     * Validate session endpoint
     * HTTP: POST /security/validateSession
     * Maps request to DTO → delegates to SecurityUseCase → returns session status
     */
    @PostMapping("/validateSession")
    public ResponseEntity<SessionValidationResponseDTO> validateSession(
            @Valid @RequestBody SessionValidationRequestDTO requestDTO) {
        log.info("Session validation requested for session: {}", requestDTO.getSessionId());
        SessionValidationResponseDTO response = securityUseCase.validateSession(requestDTO);
        return ResponseEntity.ok(response);
    }
}
