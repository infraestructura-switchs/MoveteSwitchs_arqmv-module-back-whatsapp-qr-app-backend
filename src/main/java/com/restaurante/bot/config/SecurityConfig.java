package com.restaurante.bot.config;

import com.restaurante.bot.util.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    @Qualifier("corsConfigurationSourceImpl")
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/generateToken").permitAll()
                        .requestMatchers("/getProductByCompany/**").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/change/status-ocuped/**").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/change/status-pay/**").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/restauranttable/change/status-requesting-service/**").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/restauranttable/get").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/restauranttable/delete/{tableId}").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/restauranttable/createTable/**").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/order/get").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/order/confirmation/**").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/order/status/send").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/order/no-confirmed").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/transaction/finish/{tableNumber}").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/order-delivery/get-all-orders").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/order-delivery/delete/{id}").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/order-delivery/updateStatus/{orderTransactionDeliveryId}").authenticated()
                        .requestMatchers("/api/back-whatsapp-qr-app/order-delivery/update-order").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
