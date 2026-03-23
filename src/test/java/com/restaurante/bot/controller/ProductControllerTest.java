package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.ProductUseCase;
import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductCategoryDTO;
import com.restaurante.bot.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductUseCase productUseCase;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @Test
    void getProductsByCompany_WithFormatCategories_ShouldReturnCategoriesAndMeta() throws Exception {
        when(productUseCase.getProductsSfotRestaurantByCompanyId(42L)).thenReturn(sampleCategorizedProducts());

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/42")
                        .param("format", "categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].categoryName").value("PIZZAS"))
                .andExpect(jsonPath("$.categories[0].products[0].productName").value("Muzzarella"))
                .andExpect(jsonPath("$.categories[0].products[0].comments").isArray())
                .andExpect(jsonPath("$.productsByCategory").doesNotExist())
                .andExpect(jsonPath("$.meta.companyId").value(42))
                .andExpect(jsonPath("$.meta.totalProducts").value(1))
                .andExpect(jsonPath("$.meta.fetchedAt").isNotEmpty());
    }

    @Test
    void getProductsByCompany_WithFormatMap_ShouldReturnProductsByCategoryAndMeta() throws Exception {
        when(productUseCase.getProductsSfotRestaurantByCompanyId(42L)).thenReturn(sampleCategorizedProducts());

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/42")
                        .param("format", "map"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").doesNotExist())
                .andExpect(jsonPath("$.productsByCategory.PIZZAS[0].productName").value("Muzzarella"))
                .andExpect(jsonPath("$.productsByCategory.PIZZAS[0].comments").isArray())
                .andExpect(jsonPath("$.meta.companyId").value(42))
                .andExpect(jsonPath("$.meta.totalProducts").value(1));
    }

    @Test
    void getProductsByCompany_WithFormatBoth_ShouldReturnCategoriesMapAndMeta() throws Exception {
        when(productUseCase.getProductsSfotRestaurantByCompanyId(42L)).thenReturn(sampleCategorizedProducts());

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/42")
                        .param("format", "both"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].categoryName").value("PIZZAS"))
                .andExpect(jsonPath("$.productsByCategory.PIZZAS[0].productName").value("Muzzarella"))
                .andExpect(jsonPath("$.meta.companyId").value(42))
                .andExpect(jsonPath("$.meta.totalProducts").value(1));
    }

    private CategorizedProductsDTO sampleCategorizedProducts() {
        ProductDto product = ProductDto.builder()
                .id(1L)
                .productName("Muzzarella")
                .price(1200.0)
                .comments(Collections.emptyList())
                .build();

        ProductCategoryDTO category = ProductCategoryDTO.builder()
                .categoryName("PIZZAS")
                .products(Collections.singletonList(product))
                .build();

        return CategorizedProductsDTO.builder()
                .categories(Collections.singletonList(category))
                .build();
    }
}
