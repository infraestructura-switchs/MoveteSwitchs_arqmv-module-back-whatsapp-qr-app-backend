package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.CategoryRequestDTO;
import com.restaurante.bot.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    
    List<CategoryResponseDTO> getAllCategories();
    
    List<CategoryResponseDTO> getCategoriesByCompanyId(Long companyId);
    
    CategoryResponseDTO getCategoryById(Long id);
    
    CategoryResponseDTO getCategoryByNameAndCompanyId(String name, Long companyId);
    
    CategoryResponseDTO getCategoryByName(String name);
    
    CategoryResponseDTO createCategory(CategoryRequestDTO request);
    
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request);
    
    void deleteCategory(Long id);
    
    List<CategoryResponseDTO> getCategoriesByStatus(String status);
    
    List<CategoryResponseDTO> getCategoriesByCompanyIdAndStatus(Long companyId, String status);
    
    List<CategoryResponseDTO> getCategoriesByParameterId(Long parameterId);
    
    List<CategoryResponseDTO> getCategoriesByCompanyIdAndParameterId(Long companyId, Long parameterId);
}