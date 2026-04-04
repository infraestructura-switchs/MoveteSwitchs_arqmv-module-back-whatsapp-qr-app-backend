package com.restaurante.bot.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VersionController.class)
@AutoConfigureMockMvc(addFilters = false)
class VersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildProperties buildProperties;

    @MockBean
    private HealthEndpoint healthEndpoint;

    @MockBean
    private HttpCodeStatusMapper httpCodeStatusMapper;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        when(buildProperties.getVersion()).thenReturn("0.0.10");
    }

    @Test
    void statusShouldReturn503WhenDatabaseIsDown() throws Exception {
        when(healthEndpoint.health()).thenReturn(Health.down().build());
        when(httpCodeStatusMapper.getStatusCode(Status.DOWN)).thenReturn(503);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/status"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database").value("DOWN"));
    }
}
