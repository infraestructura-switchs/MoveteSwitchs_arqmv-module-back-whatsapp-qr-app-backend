package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.CategoryRepositoryPort;
import com.restaurante.bot.model.Category;
import com.restaurante.bot.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findByCompanyId(Long companyId) {
        return categoryRepository.findByCompanyId(companyId);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Optional<Category> findByNameAndCompanyId(String name, Long companyId) {
        return categoryRepository.findByNameAndCompanyId(name, companyId);
    }

    @Override
    public List<Category> findByStatus(String status) {
        return categoryRepository.findByStatus(status);
    }

    @Override
    public List<Category> findByCompanyIdAndStatus(Long companyId, String status) {
        return categoryRepository.findByCompanyIdAndStatus(companyId, status);
    }

    @Override
    public Optional<Category> findByExternalId(Long externalId) {
        return categoryRepository.findByExternalId(externalId);
    }

    @Override
    public List<Category> findByCompanyIdAndExternalId(Long companyId, Long parameterId) {
        return categoryRepository.findByCompanyIdAndExternalId(companyId, parameterId);
    }

    @Override
    public boolean existsByNameAndCompanyId(String name, Long companyId) {
        return categoryRepository.existsByNameAndCompanyId(name, companyId);
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }
}