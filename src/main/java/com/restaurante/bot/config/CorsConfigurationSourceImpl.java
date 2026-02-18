package com.restaurante.bot.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfigurationSourceImpl implements CorsConfigurationSource {

    @Override
    public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();


        List<String> allowedOrigins = Arrays.asList(
                "http://localhost:8080",
                "http://localhost:4000",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175",
                "https://module-landing-page-qr-app-frontend-t0rn.onrender.com",
                "https://movete.is.arqbs.com",
                "https://qr-movete.saas.arqbs.com",
                "https://panel-movete.is.arqbs.com",
                "https://panel-movete.saas.arqbs.com",
                "https://qr-movete.is.arqbs.com"
        );
        corsConfiguration.setAllowedOriginPatterns(allowedOrigins);

        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));

        return corsConfiguration;
    }
}