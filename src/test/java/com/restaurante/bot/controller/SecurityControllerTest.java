package com.restaurante.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.application.ports.incoming.ShortLinkUseCase;
import com.restaurante.bot.dto.GenerateTokenRequestDTO;
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
                .companyId(273L)
                .userId(9L)
                .build();

        when(companyRepository.existsByExternalCompanyId(273L)).thenReturn(true);
        when(jwtUtil.generateSessionId()).thenReturn("9ba2153735304a0eb1b0ba67e9823e54");
        when(jwtUtil.generateToken(eq(273L), eq(9L), anyString()))
                .thenAnswer(invocation -> "jwt-" + invocation.getArgument(2, String.class));

        mockMvc.perform(post("/api/back-whatsapp-qr-app/security/generateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", startsWith("jwt-")))
                .andExpect(jsonPath("$.session_id", matchesPattern("^[a-f0-9]{32}$")));

        verify(sessionRegistryService).registerSession(org.mockito.ArgumentMatchers.anyString(), eq(273L), eq(9L));
    }

    @Test
    void logoutShouldInvalidateSession() throws Exception {
        when(jwtUtil.isTokenValid("jwt-token")).thenReturn(true);
        when(jwtUtil.extractSessionId("jwt-token")).thenReturn("session-123");

        mockMvc.perform(post("/api/back-whatsapp-qr-app/security/logout")
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sesion cerrada correctamente"))
                .andExpect(jsonPath("$.session_id").value("session-123"));

        verify(sessionRegistryService).invalidateSession("session-123");
    }

        @Test
        void validateSessionShouldReportActiveStateAndExpiration() throws Exception {
                SessionValidationRequestDTO request = SessionValidationRequestDTO.builder()
                                .sessionId("session-123")
                                .build();

                when(sessionRegistryService.getSessionStatus("session-123"))
                                .thenReturn(new SessionRegistryService.SessionStatus(
                                                "session-123",
                                                true,
                                                false,
                                                java.time.Instant.parse("2026-03-18T12:00:00Z"),
                                                900000L));

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