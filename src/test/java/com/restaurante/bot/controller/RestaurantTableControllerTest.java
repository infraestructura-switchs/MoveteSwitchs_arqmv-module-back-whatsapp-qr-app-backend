package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.RestaurantTableUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantTableController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantTableUseCase restaurantTableInterface;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @Test
    void addTable_ShouldReturnBadRequest_WhenTableNumberIsMissing() throws Exception {
        mockMvc.perform(post("/api/back-whatsapp-qr-app/restauranttable/createTable"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Falta el parametro obligatorio: tableNumber"));
    }

    @Test
    void addTable_ShouldReturnBadRequest_WhenTableNumberIsInvalid() throws Exception {
        mockMvc.perform(post("/api/back-whatsapp-qr-app/restauranttable/createTable")
                .param("tableNumber", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El parametro 'tableNumber' debe ser de tipo Long"));
    }

    @Test
    void addTable_ShouldReturnBadRequest_WhenTableNumberIsNotPositive() throws Exception {
        mockMvc.perform(post("/api/back-whatsapp-qr-app/restauranttable/createTable")
                .param("tableNumber", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Campos con valor invalido: tableNumber"));
    }
}