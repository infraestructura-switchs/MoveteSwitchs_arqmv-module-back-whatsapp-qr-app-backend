package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.CompanyUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
        MockMultipartFile companyPart = new MockMultipartFile("company", "company", "application/json", "{}".getBytes());
        when(companyUseCase.save(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
            .thenThrow(new DomainException(DomainErrorCode.INVALID_REQUEST, "Campos obligatorios faltantes: companyName, longitude, latitude, apiKey, baseValue, additionalValue, cityId"));

        mockMvc.perform(multipart("/api/back-whatsapp-qr-app/company/create")
                .file(companyPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Campos obligatorios faltantes: companyName, longitude, latitude, apiKey, baseValue, additionalValue, cityId"));
    }

    @Test
    void updateCompany_ShouldReturnBadRequest_WhenApiKeyIsBlank() throws Exception {
        String companyJson = "{\"nameCompany\":\"Mi Empresa\", \"apiKey\":\"\"}";
        MockMultipartFile companyPart = new MockMultipartFile("company", "company", "application/json", companyJson.getBytes());
        when(companyUseCase.update(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
            .thenThrow(new DomainException(DomainErrorCode.INVALID_REQUEST, "Campos obligatorios faltantes: apiKey"));

        mockMvc.perform(multipart("/api/back-whatsapp-qr-app/company/update/1")
                .file(companyPart)
                .with(request -> { request.setMethod("PUT"); return request; }))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Campos obligatorios faltantes: apiKey"));
    }

    @Test
    void getCompanies_ShouldReturnServiceUnavailable_WhenDatabaseIsDown() throws Exception {
        when(companyUseCase.getAllPageCompany(anyInt(), anyInt(), anyString(), anyString()))
            .thenThrow(new DataAccessResourceFailureException("Database unavailable"));

        mockMvc.perform(get("/api/back-whatsapp-qr-app/company"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.status").value(503))
            .andExpect(jsonPath("$.error").value("Service Unavailable"))
            .andExpect(jsonPath("$.message").value("No se pudo conectar a la base de datos. Intente nuevamente más tarde."));
    }

    // externalId invalid test removed: no longer applicable after refactor
}