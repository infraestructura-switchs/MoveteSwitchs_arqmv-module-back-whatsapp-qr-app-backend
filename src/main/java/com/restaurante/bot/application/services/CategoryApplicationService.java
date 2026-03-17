package com.restaurante.bot.application.services;

import com.restaurante.bot.application.ports.incoming.CategoryUseCase;
import com.restaurante.bot.application.ports.outgoing.CategoryRepositoryPort;
import com.restaurante.bot.dto.CategoryRequestDTO;
import com.restaurante.bot.dto.CategoryResponseDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Service("categoryApplicationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryApplicationService implements CategoryUseCase {
    private final CategoryRepositoryPort categoryRepo;

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepo.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByCompanyId(Long companyId) {
        return categoryRepo.findByCompanyId(companyId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new GenericException("Category not found", org.springframework.http.HttpStatus.NOT_FOUND));
        return mapToResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO getCategoryByNameAndCompanyId(String name, Long companyId) {
        Category category = categoryRepo.findByNameAndCompanyId(name, companyId)
                .orElseThrow(() -> new GenericException("Category not found", org.springframework.http.HttpStatus.NOT_FOUND));
        return mapToResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO getCategoryByName(String name) {
        Category category = categoryRepo.findByName(name)
                .orElseThrow(() -> new GenericException("Category not found", org.springframework.http.HttpStatus.NOT_FOUND));
        return mapToResponseDTO(category);
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepo.existsByNameAndCompanyId(request.getName(), request.getCompanyId())) {
            throw new GenericException("Category with this name already exists", org.springframework.http.HttpStatus.CONFLICT);
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setStatus(request.getStatus());
        category.setCompanyId(request.getCompanyId());
        category.setExternalId(request.getParameterId());

        Category saved = categoryRepo.save(category);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new GenericException("Category not found", org.springframework.http.HttpStatus.NOT_FOUND));

        if (!category.getName().equals(request.getName()) &&
            categoryRepo.existsByNameAndCompanyId(request.getName(), request.getCompanyId())) {
            throw new GenericException("Category with this name already exists", org.springframework.http.HttpStatus.CONFLICT);
        }

        category.setName(request.getName());
        category.setStatus(request.getStatus());
        category.setCompanyId(request.getCompanyId());
        category.setExternalId(request.getParameterId());
        Category updated = categoryRepo.save(category);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }

    @Override
    public Page<CategoryResponseDTO> getAll(Map<String, String> customQuery) {
        List<CategoryResponseDTO> all = getAllCategories();
        return new PageImpl<>(all);
    }

    @Override
    public Page<CategoryResponseDTO> getAll(int page, int size, String orders, String sortBy) {
        List<CategoryResponseDTO> all = getAllCategories();
        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<CategoryResponseDTO> content = start >= all.size() ? List.of() : all.subList(start, end);
        return new PageImpl<>(content, PageRequest.of(page, size), all.size());
    }

    @Override
    public List<CategoryResponseDTO> getAllWithOutPage(Map<String, String> customQuery) {
        return getAllCategories();
    }

    @Override
    public Page<CategoryResponseDTO> searchCustom(Map<String, String> customQuery) {
        List<CategoryResponseDTO> all = getAllCategories();
        return new PageImpl<>(all);
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByStatus(String status) {
        return categoryRepo.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByCompanyIdAndStatus(Long companyId, String status) {
        return categoryRepo.findByCompanyIdAndStatus(companyId, status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByParameterId(Long parameterId) {
        return categoryRepo.findByExternalId(parameterId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByCompanyIdAndParameterId(Long companyId, Long parameterId) {
        return categoryRepo.findByCompanyIdAndExternalId(companyId, parameterId).stream()
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