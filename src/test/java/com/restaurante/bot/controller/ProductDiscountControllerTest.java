package com.restaurante.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.application.ports.incoming.ProductDiscountCrudUseCase;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.dto.ProductDiscountDto;
import com.restaurante.bot.dto.ProductDiscountCreateDto;
import com.restaurante.bot.dto.ProductDiscountSaveAndUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        ProductDiscountCreateDto request = ProductDiscountCreateDto.builder()
                .productId(8L)
                .companyId(273L)
                                .description("Promo almuerzo")
                .discountAmount(5000.0)
                .build();

        ProductDiscountDto response = ProductDiscountDto.builder()
                .id(1L)
                .productId(8L)
                .companyId(273L)
                .discountAmount(5000.0)
                .status("ACTIVE")
                .build();

        when(productDiscountCrudUseCase.save(any(ProductDiscountCreateDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/back-whatsapp-qr-app/admin/product-discount/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.discountAmount").value(5000.0));
    }

    @Test
    void createDiscount_ShouldReturnBadRequest_WhenRequiredFieldsAreMissing() throws Exception {
        when(productDiscountCrudUseCase.save(any(ProductDiscountCreateDto.class)))
                .thenThrow(new DomainException(DomainErrorCode.INVALID_REQUEST,
                        "Campos obligatorios faltantes: productId, companyId, description, discountAmount"));

        mockMvc.perform(post("/api/back-whatsapp-qr-app/admin/product-discount/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDiscount_ShouldReturnOk() throws Exception {
        ProductDiscountSaveAndUpdateDto request = ProductDiscountSaveAndUpdateDto.builder()
                .productId(8L)
                .companyId(273L)
                .description("Promo tarde")
                .discountAmount(2500.0)
                .status("ACTIVE")
                .build();

        ProductDiscountDto response = ProductDiscountDto.builder()
                .id(1L)
                .productId(8L)
                .companyId(273L)
                .discountAmount(2500.0)
                .status("ACTIVE")
                .build();

        when(productDiscountCrudUseCase.update(eq(1L), any(ProductDiscountSaveAndUpdateDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/back-whatsapp-qr-app/admin/product-discount/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void updateDiscount_ShouldReturnBadRequest_WhenStatusIsMissing() throws Exception {
        when(productDiscountCrudUseCase.update(eq(1L), any(ProductDiscountSaveAndUpdateDto.class)))
                .thenThrow(new DomainException(DomainErrorCode.INVALID_REQUEST,
                        "Campos obligatorios faltantes: productId, companyId, description, discountAmount, status"));

        String requestWithoutStatus = """
                {
                  "productId": 8,
                  "companyId": 273,
                  "description": "Promo",
                  "discountAmount": 2500
                }
                """;

        mockMvc.perform(put("/api/back-whatsapp-qr-app/admin/product-discount/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithoutStatus))
                .andExpect(status().isBadRequest());
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