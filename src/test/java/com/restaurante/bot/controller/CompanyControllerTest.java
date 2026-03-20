package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.CompanyUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyUseCase companyUseCase;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @Test
    void createCompany_ShouldReturnBadRequest_WhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(multipart("/api/back-whatsapp-qr-app/company/create")
                        .param("apiKey", ""))
                .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Campos obligatorios faltantes: companyName, longitude, latitude, apiKey, baseValue, additionalValue, cityId"));
    }

    @Test
    void updateCompany_ShouldReturnBadRequest_WhenApiKeyIsBlank() throws Exception {
        mockMvc.perform(multipart("/api/back-whatsapp-qr-app/company/updateByCompanynId/1")
                        .param("companyName", "Mi Empresa")
                        .param("apiKey", "")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Campos obligatorios faltantes: apiKey"));
    }

    @Test
    void updateCompany_ShouldReturnBadRequest_WhenExternalIdIsInvalid() throws Exception {
        mockMvc.perform(multipart("/api/back-whatsapp-qr-app/company/updateByCompanynId/1")
                        .param("companyName", "Mi Empresa")
                        .param("apiKey", "test-api-key")
                        .param("externalId", "null")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El parametro 'externalId' debe ser de tipo Long"));
    }
}