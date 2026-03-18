package com.restaurante.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.application.ports.incoming.ProductDiscountCrudUseCase;
import com.restaurante.bot.dto.ProductDiscountDto;
import com.restaurante.bot.dto.ProductDiscountSaveAndUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductDiscountController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductDiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductDiscountCrudUseCase productDiscountCrudUseCase;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @Test
    void createDiscount_ShouldReturnOk() throws Exception {
        ProductDiscountSaveAndUpdateDto request = ProductDiscountSaveAndUpdateDto.builder()
                .productId(8L)
                .companyId(273L)
                .discountAmount(5000.0)
                .startAt(LocalDateTime.of(2026, 3, 18, 10, 0))
                .endAt(LocalDateTime.of(2026, 3, 18, 18, 0))
                .build();

        ProductDiscountDto response = ProductDiscountDto.builder()
                .id(1L)
                .productId(8L)
                .companyId(273L)
                .discountAmount(5000.0)
                .status("ACTIVE")
                .build();

        when(productDiscountCrudUseCase.save(any(ProductDiscountSaveAndUpdateDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/back-whatsapp-qr-app/admin/product-discount/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.discountAmount").value(5000.0));
    }

    @Test
    void createDiscount_ShouldReturnBadRequest_WhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/back-whatsapp-qr-app/admin/product-discount/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Campos obligatorios faltantes: productId, companyId, discountAmount, startAt, endAt"));
    }

    @Test
    void getDiscount_ShouldReturnOk() throws Exception {
        ProductDiscountDto response = ProductDiscountDto.builder()
                .id(1L)
                .productId(8L)
                .companyId(273L)
                .discountAmount(5000.0)
                .status("ACTIVE")
                .build();

        when(productDiscountCrudUseCase.get(eq(1L), eq(273L))).thenReturn(response);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/admin/product-discount/1")
                        .param("companyId", "273"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(8L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}