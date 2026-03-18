package com.restaurante.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.application.ports.incoming.ProductCrudUseCase;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoCrudController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoCrudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductCrudUseCase productCrudUseCase;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDto sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductDto();
        sampleProduct.setId(1L);
        sampleProduct.setProductName("Test Product");
        sampleProduct.setPrice(9.99);
        sampleProduct.setDescription("Desc");
        sampleProduct.setStatus("ACTIVE");
    }

    @Test
    void createProduct_ShouldReturnOk() throws Exception {
        when(productCrudUseCase.save(any(ProductSaveAndUpdateDto.class))).thenReturn(sampleProduct);

        ProductSaveAndUpdateDto request = new ProductSaveAndUpdateDto();
        request.setProductName("Test Product");
        request.setPrice(9.99);

        mockMvc.perform(post("/api/back-whatsapp-qr-app/producto/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"));
    }

    @Test
    void getProductById_ShouldReturnOk() throws Exception {
        when(productCrudUseCase.get(1L)).thenReturn(sampleProduct);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"));
    }

        @Test
        void updateProduct_WithArrayInformation_ShouldReturnOk() throws Exception {
                when(productCrudUseCase.update(any(Long.class), any(ProductSaveAndUpdateDto.class))).thenReturn(sampleProduct);

                String request = """
                                {
                                    \"productName\": \"Test Product\",
                                    \"price\": 9.99,
                                    \"companyId\": 273,
                                    \"information\": [],
                                    \"comments\": [\"sinaguacuate\"]
                                }
                                """;

                mockMvc.perform(put("/api/back-whatsapp-qr-app/producto/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.productName").value("Test Product"));
        }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        when(productCrudUseCase.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/back-whatsapp-qr-app/producto/delete/1"))
                .andExpect(status().isNoContent());
    }
}
