package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.CategoryService;
import com.restaurante.bot.dto.CategoryRequestDTO;
import com.restaurante.bot.dto.CategoryResponseDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Category;
import com.restaurante.bot.repository.CategoryRepository;
import com.restaurante.bot.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ParameterRepository parameterRepository;

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByCompanyId(Long companyId) {
        return categoryRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new GenericException("Category not found", HttpStatus.NOT_FOUND));
        return mapToResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new GenericException("Category not found", HttpStatus.NOT_FOUND));
        return mapToResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO getCategoryByNameAndCompanyId(String name, Long companyId) {
        Category category = categoryRepository.findByNameAndCompanyId(name, companyId)
                .orElseThrow(() -> new GenericException("Category not found", HttpStatus.NOT_FOUND));
        return mapToResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.existsByNameAndCompanyId(request.getName(), request.getCompanyId())) {
            throw new GenericException("Category with this name already exists", HttpStatus.CONFLICT);
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setStatus(request.getStatus());
        category.setCompanyId(request.getCompanyId());
        category.setExternalId(request.getParameterId());


        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDTO(savedCategory);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new GenericException("Category not found", HttpStatus.NOT_FOUND));

        if (!category.getName().equals(request.getName()) && 
            categoryRepository.existsByNameAndCompanyId(request.getName(), request.getCompanyId())) {
            throw new GenericException("Category with this name already exists", HttpStatus.CONFLICT);
        }

        category.setName(request.getName());
        category.setStatus(request.getStatus());
        category.setCompanyId(request.getCompanyId());
        category.setExternalId(request.getParameterId());
        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByStatus(String status) {
        return categoryRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByCompanyIdAndStatus(Long companyId, String status) {
        return categoryRepository.findByCompanyIdAndStatus(companyId, status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByParameterId(Long parameterId) {
        return categoryRepository.findByExternalId(parameterId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByCompanyIdAndParameterId(Long companyId, Long parameterId) {
        return categoryRepository.findByCompanyIdAndExternalId(companyId, parameterId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .parameterId(category.getExternalId() != null ? category.getExternalId() : null)
                .parameterName(category.getExternalId() != null ? category.getName() : null)
                .status(category.getStatus())
                .companyId(category.getCompanyId())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}