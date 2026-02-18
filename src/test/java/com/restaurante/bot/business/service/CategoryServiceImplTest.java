package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.CategoryRequestDTO;
import com.restaurante.bot.dto.CategoryResponseDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Category;
import com.restaurante.bot.repository.CategoryRepository;
import com.restaurante.bot.repository.ParameterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ParameterRepository parameterRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequestDTO categoryRequestDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setName("Test Category");
        category.setStatus("ACTIVE");
        category.setCompanyId(10L);
        category.setExternalId(100L);

        categoryRequestDTO = new CategoryRequestDTO();
        categoryRequestDTO.setName("Test Category");
        categoryRequestDTO.setStatus("ACTIVE");
        categoryRequestDTO.setCompanyId(10L);
        categoryRequestDTO.setParameterId(100L);
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category.getName(), result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_WhenFound_ShouldReturnDTO() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponseDTO result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(category.getName(), result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_WhenNotFound_ShouldThrowException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        GenericException exception = assertThrows(GenericException.class, () -> 
            categoryService.getCategoryById(1L)
        );

        assertEquals("Category not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createCategory_WhenNotExists_ShouldCreateAndReturnDTO() {
        when(categoryRepository.existsByNameAndCompanyId(anyString(), anyLong())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.createCategory(categoryRequestDTO);

        assertNotNull(result);
        assertEquals(category.getName(), result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_WhenExists_ShouldThrowException() {
        when(categoryRepository.existsByNameAndCompanyId(anyString(), anyLong())).thenReturn(true);

        GenericException exception = assertThrows(GenericException.class, () -> 
            categoryService.createCategory(categoryRequestDTO)
        );

        assertEquals("Category with this name already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void deleteCategory_ShouldCallRepository() {
        categoryService.deleteCategory(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
