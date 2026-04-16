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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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

    // ---------------------------------------------------------------
    // Tests for GET /getProductByCompany/{externalCompanyId}/paged
    // ---------------------------------------------------------------

    @Test
    void getProductsByCompanyPaged_DefaultParams_ShouldReturnPage() throws Exception {
        ProductDto product = sampleProduct();
        Page<ProductDto> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);

        when(productUseCase.getProductsByCompanyPaged(eq(42L), eq(0), eq(10),
                eq("ASC"), eq("productId"), isNull(), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/42/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Muzzarella"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getProductsByCompanyPaged_CustomPageAndSize_ShouldPassParamsToService() throws Exception {
        Page<ProductDto> page = new PageImpl<>(List.of(sampleProduct()), PageRequest.of(1, 5), 6);

        when(productUseCase.getProductsByCompanyPaged(eq(42L), eq(1), eq(5),
                eq("ASC"), eq("productId"), isNull(), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/42/paged")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(6));
    }

    @Test
    void getProductsByCompanyPaged_WithNameFilter_ShouldPassNameToService() throws Exception {
        Page<ProductDto> page = new PageImpl<>(List.of(sampleProduct()), PageRequest.of(0, 10), 1);

        when(productUseCase.getProductsByCompanyPaged(eq(42L), eq(0), eq(10),
                eq("ASC"), eq("productId"), eq("Muzz"), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/42/paged")
                        .param("name", "Muzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Muzzarella"));
    }

    @Test
    void getProductsByCompanyPaged_WithCategoryFilter_ShouldPassCategoryToService() throws Exception {
        Page<ProductDto> page = new PageImpl<>(List.of(sampleProduct()), PageRequest.of(0, 10), 1);

        when(productUseCase.getProductsByCompanyPaged(eq(42L), eq(0), eq(10),
                eq("ASC"), eq("productId"), isNull(), eq("PIZZAS")))
                .thenReturn(page);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/42/paged")
                        .param("category", "PIZZAS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Muzzarella"));
    }

    @Test
    void getProductsByCompanyPaged_EmptyResult_ShouldReturnEmptyPage() throws Exception {
        Page<ProductDto> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(productUseCase.getProductsByCompanyPaged(eq(99L), eq(0), eq(10),
                eq("ASC"), eq("productId"), isNull(), isNull()))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/99/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getProductsByCompanyPaged_DescOrder_ShouldPassOrderToService() throws Exception {
        Page<ProductDto> page = new PageImpl<>(List.of(sampleProduct()), PageRequest.of(0, 10), 1);

        when(productUseCase.getProductsByCompanyPaged(eq(42L), eq(0), eq(10),
                eq("DESC"), eq("price"), isNull(), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/product/getProductByCompany/42/paged")
                        .param("orders", "DESC")
                        .param("sortBy", "price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName").value("Muzzarella"));
    }

    private ProductDto sampleProduct() {
        return ProductDto.builder()
                .id(1L)
                .productName("Muzzarella")
                .price(1200.0)
                .comments(Collections.emptyList())
                .build();
    }
}
