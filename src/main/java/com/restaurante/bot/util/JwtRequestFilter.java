package com.restaurante.bot.util;

import io.jsonwebtoken.*;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

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
                Claims claims = jwtUtil.extractAllClaims(token);  // Usa el método de JwtUtil para extraer claims
                log.debug("Claims extracted: {}", claims);

                if (jwtUtil.isTokenValid(token)) {
                    Long companyId = jwtUtil.extractCompanyId(token);
                    log.debug("Token valid, extracted companyId: {}", companyId);

                    // Opcional: Si necesitas authorities (del código original de security), extrae aquí
                    @SuppressWarnings("unchecked")
                    List<String> authorities = (List<String>) claims.get("authorities");

                    UsernamePasswordAuthenticationToken authToken;
                    if (authorities != null && !authorities.isEmpty()) {
                        authToken = new UsernamePasswordAuthenticationToken(companyId, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                    } else {
                        authToken = new UsernamePasswordAuthenticationToken(companyId, null, null);  // Sin authorities si no hay
                    }

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication set in SecurityContext");
                } else {
                    log.warn("Token invalid");
                    SecurityContextHolder.clearContext();
                }
            } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
                log.error("Token validation error: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                return;  // Detiene el filtro si hay error grave
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
}