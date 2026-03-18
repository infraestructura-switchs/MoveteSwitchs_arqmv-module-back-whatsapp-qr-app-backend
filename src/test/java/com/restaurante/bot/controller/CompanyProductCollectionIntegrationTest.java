package com.restaurante.bot.controller;

import com.cloudinary.Cloudinary;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class CompanyProductCollectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Cloudinary cloudinary;

    @Test
    void createCompanyWithoutLogo_ShouldNotReturn500() throws Exception {
        mockMvc.perform(multipart("/api/back-whatsapp-qr-app/company/create")
                        .param("companyName", "Mi Empresa")
                        .param("whatsappNumber", "+573001234567")
                        .param("longitude", "-74.0")
                        .param("latitude", "4.6")
                        .param("baseValue", "0.0")
                        .param("additionalValue", "0.0")
                        .param("externalId", "123")
                        .param("cityId", "1")
                        .param("apiKey", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameCompany").value("Mi Empresa"));
    }

    @Test
    void getMissingCompany_ShouldReturn404InsteadOf500() throws Exception {
        mockMvc.perform(get("/api/back-whatsapp-qr-app/company/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Empresa no encontrada con id 999999"));
    }

    @Test
    void deleteMissingCompany_ShouldReturn404InsteadOf500() throws Exception {
        mockMvc.perform(delete("/api/back-whatsapp-qr-app/company/delete/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("La compañia no fue encontrada por el id 999999"));
    }

    @Test
    void updateMissingCompany_ShouldReturn404InsteadOf500() throws Exception {
        mockMvc.perform(multipart("/api/back-whatsapp-qr-app/company/updateByCompanynId/999999")
                        .param("companyName", "Mi Empresa Modificada")
                        .param("apiKey", "test-api-key")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Empresa con ID 999999 no existe"));
    }

    @Test
    void getMissingProductCrud_ShouldReturn404InsteadOf500() throws Exception {
        mockMvc.perform(get("/api/back-whatsapp-qr-app/admin/product/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }

    @Test
    void updateMissingProductCrud_ShouldReturn404InsteadOf500() throws Exception {
        ProductSaveAndUpdateDto request = ProductSaveAndUpdateDto.builder()
                .productName("Pizza Hawaiana")
                .price(13000.0)
                .companyId(1L)
                .build();

        mockMvc.perform(put("/api/back-whatsapp-qr-app/admin/product/update/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Producto no existe"));
    }
}