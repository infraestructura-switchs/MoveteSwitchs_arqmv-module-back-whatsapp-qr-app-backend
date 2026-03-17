package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.dto.CategoryRequestDTO;
import com.restaurante.bot.dto.CategoryResponseDTO;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface CategoryUseCase {
    List<CategoryResponseDTO> getAllCategories();
    List<CategoryResponseDTO> getCategoriesByCompanyId(Long companyId);
    CategoryResponseDTO getCategoryById(Long id);
    CategoryResponseDTO getCategoryByNameAndCompanyId(String name, Long companyId);
    CategoryResponseDTO getCategoryByName(String name);
    Page<CategoryResponseDTO> getAll(Map<String, String> customQuery);

    Page<CategoryResponseDTO> getAll(int page, int size, String orders, String sortBy);

    List<CategoryResponseDTO> getAllWithOutPage(Map<String, String> customQuery);

    Page<CategoryResponseDTO> searchCustom(Map<String, String> customQuery);
    CategoryResponseDTO createCategory(CategoryRequestDTO request);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request);
    void deleteCategory(Long id);
    List<CategoryResponseDTO> getCategoriesByStatus(String status);
    List<CategoryResponseDTO> getCategoriesByCompanyIdAndStatus(Long companyId, String status);
    List<CategoryResponseDTO> getCategoriesByParameterId(Long parameterId);
    List<CategoryResponseDTO> getCategoriesByCompanyIdAndParameterId(Long companyId, Long parameterId);
}