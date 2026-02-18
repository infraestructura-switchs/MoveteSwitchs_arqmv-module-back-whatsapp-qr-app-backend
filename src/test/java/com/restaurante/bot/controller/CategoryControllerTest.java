package com.restaurante.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.business.interfaces.CategoryService;
import com.restaurante.bot.dto.CategoryRequestDTO;
import com.restaurante.bot.dto.CategoryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = CategoryResponseDTO.builder()
                .categoryId(1L)
                .name("Test Category")
                .status("ACTIVE")
                .companyId(10L)
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/back-whatsapp-qr-app/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    void getCategoryById_ShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/back-whatsapp-qr-app/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void createCategory_ShouldReturnCreated() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Test Category");
        requestDTO.setStatus("ACTIVE");
        requestDTO.setCompanyId(10L);

        when(categoryService.createCategory(any(CategoryRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/back-whatsapp-qr-app/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/back-whatsapp-qr-app/categories/1"))
                .andExpect(status().isNoContent());
    }
}
