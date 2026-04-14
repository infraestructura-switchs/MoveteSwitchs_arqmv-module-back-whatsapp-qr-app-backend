package com.restaurante.bot.util;

import com.restaurante.bot.security.SessionRegistryService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.restaurante.bot.exception.ErrorMessageService;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SessionRegistryService sessionRegistryService;

    @Mock
    private ErrorMessageService messageService;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void shouldRejectRequestWhenSessionIsInactive() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        Claims claims = mock(Claims.class);

        Mockito.lenient().when(jwtUtil.extractAllClaims("jwt-token")).thenReturn(claims);
        Mockito.lenient().when(jwtUtil.isTokenValid("jwt-token")).thenReturn(true);
        Mockito.lenient().when(jwtUtil.extractExternalCompanyId("jwt-token")).thenReturn(273L);
        Mockito.lenient().when(jwtUtil.extractSessionId("jwt-token")).thenReturn("session-123");
        Mockito.lenient().when(sessionRegistryService.isSessionActive("session-123")).thenReturn(false);
        Mockito.lenient().when(messageService.getMessage("session.invalid")).thenReturn("session.invalid");

        jwtRequestFilter.doFilter(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }
}