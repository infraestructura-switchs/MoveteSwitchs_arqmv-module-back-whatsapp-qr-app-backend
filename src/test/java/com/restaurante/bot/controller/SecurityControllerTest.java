package com.restaurante.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.application.ports.incoming.ShortLinkUseCase;
import com.restaurante.bot.application.ports.incoming.SecurityUseCase;
import com.restaurante.bot.dto.GenerateTokenRequestDTO;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.dto.SessionValidationRequestDTO;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.security.SessionRegistryService;
import com.restaurante.bot.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SecurityController.class, properties = {
        "landing.page.url=http://localhost/landing",
        "backend.url=http://localhost:8080",
        "app.request.mapping=api/back-whatsapp-qr-app"
})
@AutoConfigureMockMvc(addFilters = false)
class SecurityControllerTest {

        private static final Pattern SESSION_ID_PATTERN = Pattern.compile("^[a-f0-9]{32}$");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

        @MockBean
        private SecurityUseCase securityUseCase;
    @MockBean
    private CompanyRepository companyRepository;

    @MockBean
    private ShortLinkUseCase shortLinkservice;

        @MockBean
        private SessionRegistryService sessionRegistryService;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @Test
    void generateTokenShouldReturnTokenAndSessionId() throws Exception {
        GenerateTokenRequestDTO request = GenerateTokenRequestDTO.builder()
                .externalCompanyId(273L)
                .userId(9L)
                .apiKey("valid-key")
                .build();

        com.restaurante.bot.dto.GenerateTokenResponseDTO response = com.restaurante.bot.dto.GenerateTokenResponseDTO.builder()
                .token("jwt-abc")
                .sessionId("9ba2153735304a0eb1b0ba67e9823e54")
                .build();

        when(securityUseCase.generateToken(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/api/back-whatsapp-qr-app/security/generateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", startsWith("jwt-")))
                .andExpect(jsonPath("$.session_id", matchesPattern("^[a-f0-9]{32}$")));

        verify(securityUseCase).generateToken(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void generateTokenShouldReturnBadRequestWhenApiKeyMissing() throws Exception {
        GenerateTokenRequestDTO request = GenerateTokenRequestDTO.builder()
                .externalCompanyId(273L)
                .userId(9L)
                .build();

                when(companyRepository.existsByExternalCompanyId(273L)).thenReturn(true);
        Company company = Company.builder().id(1L).externalCompanyId(273L).apiKey("valid-key").build();
        when(companyRepository.findByExternalCompanyId(273L)).thenReturn(company);

        // Controller delegates validation to SecurityUseCase; simulate BAD_REQUEST via SecurityUseCase
        when(securityUseCase.generateToken(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new com.restaurante.bot.domain.exception.DomainException(
                        com.restaurante.bot.domain.exception.DomainErrorCode.INVALID_REQUEST,
                        "apiKey obligatorio"));

        var result = mockMvc.perform(post("/api/back-whatsapp-qr-app/security/generateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        int status = result.getResponse().getStatus();
        org.junit.jupiter.api.Assertions.assertEquals(400, status);
        org.skyscreamer.jsonassert.JSONAssert.assertEquals("{\"message\":\"apiKey obligatorio\"}", result.getResponse().getContentAsString(), false);
    }

    @Test
    void generateTokenShouldReturnUnauthorizedWhenApiKeyInvalid() throws Exception {
        GenerateTokenRequestDTO request = GenerateTokenRequestDTO.builder()
                .externalCompanyId(273L)
                .userId(9L)
                .apiKey("invalid-key")
                .build();

        when(securityUseCase.generateToken(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new com.restaurante.bot.domain.exception.DomainException(
                        com.restaurante.bot.domain.exception.DomainErrorCode.UNAUTHORIZED,
                        "apiKey invalido"));

        mockMvc.perform(post("/api/back-whatsapp-qr-app/security/generateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("apiKey invalido"));
    }

    @Test
    void logoutShouldInvalidateSession() throws Exception {
        org.mockito.Mockito.doNothing().when(securityUseCase).logout(org.mockito.ArgumentMatchers.anyString());

        mockMvc.perform(post("/api/back-whatsapp-qr-app/security/logout")
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Session closed successfully"));

        verify(securityUseCase).logout(org.mockito.ArgumentMatchers.anyString());
    }

        @Test
        void validateSessionShouldReportActiveStateAndExpiration() throws Exception {
                SessionValidationRequestDTO request = SessionValidationRequestDTO.builder()
                                .sessionId("session-123")
                                .build();

                com.restaurante.bot.dto.SessionValidationResponseDTO resp = com.restaurante.bot.dto.SessionValidationResponseDTO.builder()
                                .sessionId("session-123")
                                .active(true)
                                .expired(false)
                                .expiresAt(java.time.Instant.parse("2026-03-18T12:00:00Z"))
                                .remainingMs(900000L)
                                .build();

                when(securityUseCase.validateSession(org.mockito.ArgumentMatchers.any()))
                                .thenReturn(resp);

                mockMvc.perform(post("/api/back-whatsapp-qr-app/security/validateSession")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.session_id").value("session-123"))
                                .andExpect(jsonPath("$.active").value(true))
                                .andExpect(jsonPath("$.expired").value(false))
                                .andExpect(jsonPath("$.remaining_ms").value(900000))
                                .andExpect(jsonPath("$.expires_at").value("2026-03-18T12:00:00Z"));
        }
}