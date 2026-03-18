package com.restaurante.bot.adapters.inbound.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.application.ports.incoming.UserUseCase;
import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.UserDto;
import com.restaurante.bot.util.LoginMode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @Test
    void loginShouldExposeSessionIdInResponse() throws Exception {
        UserDto response = UserDto.builder()
                .userId(9L)
                .login("admin")
                .token("jwt-token")
                .sessionId("session-123")
                .build();

        LoginIn request = LoginIn.builder()
                .username("admin")
                .password("secret")
                .loginMode(LoginMode.GGP_LOGIN)
                .build();

        when(userUseCase.login(any(LoginIn.class))).thenReturn(response);

        mockMvc.perform(post("/api/back-whatsapp-qr-app/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.session_id").value("session-123"));
    }
}