package com.restaurante.bot.util;

import io.jsonwebtoken.*;
import com.restaurante.bot.security.SessionRegistryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SessionRegistryService sessionRegistryService;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.restaurante.bot.exception.ErrorMessageService messageService;

    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HEADER);
        log.debug("Authorization header received: {}", authorizationHeader);  // Verifica header

        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(PREFIX)) {
            token = authorizationHeader.substring(7);
            log.debug("Token extracted: {}", token);  // Verifica token

            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                log.debug("Claims extracted: {}", claims);

                if (jwtUtil.isTokenValid(token)) {
                    Long externalCompanyId = jwtUtil.extractExternalCompanyId(token);
                    String sessionId = jwtUtil.extractSessionId(token);
                    log.debug("Token valid, extracted externalCompanyId: {}", externalCompanyId);

                    // ✅ Validar que externalCompanyId no sea null
                    if (externalCompanyId == null) {
                        log.warn("Token has null externalCompanyId");
                        sendUnauthorizedError(response, "Invalid token: missing company");
                        return;
                    }

                    // ✅ Validar sesión y procesar con messageService seguro
                    if (sessionId == null || !sessionRegistryService.isSessionActive(sessionId)) {
                        log.warn("Invalid or inactive session for token, sessionId={}", sessionId);
                        String errorMsg = "Session invalid or expired";
                        if (messageService != null) {
                            errorMsg = messageService.getMessage("session.invalid");
                        }
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMsg);
                        return;
                    }

                    // ✅ Cast seguro de authorities con instanceof
                    List<String> authorities = null;
                    Object authObj = claims.get("authorities");
                    if (authObj instanceof List<?>) {
                        try {
                            @SuppressWarnings("unchecked")
                            List<String> parsedAuth = (List<String>) authObj;
                            authorities = parsedAuth;
                        } catch (ClassCastException e) {
                            log.warn("Invalid authorities format in token", e);
                        }
                    }

                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            externalCompanyId,
                            null,
                            authorities != null && !authorities.isEmpty()
                                ? authorities.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                                : null
                        );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication set in SecurityContext for company: {}", externalCompanyId);
                } else {
                    log.warn("Token invalid");
                    SecurityContextHolder.clearContext();
                }
            } catch (ExpiredJwtException e) {
                log.warn("Token expired: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorMsg = "Token expired";
                if (messageService != null) {
                    errorMsg = messageService.getMessage("token.expired");
                }
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMsg);
                return;
            } catch (UnsupportedJwtException | MalformedJwtException e) {
                log.error("Token validation error: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                String errorMsg = "Token invalid";
                if (messageService != null) {
                    errorMsg = messageService.getMessage("token.invalid");
                }
                response.sendError(HttpServletResponse.SC_FORBIDDEN, errorMsg);
                return;
            } catch (Exception e) {
                log.error("Unexpected error during token processing: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("No valid Authorization header found");
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message) 
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}