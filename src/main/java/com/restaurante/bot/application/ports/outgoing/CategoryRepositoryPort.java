package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    List<Category> findAll();
    List<Category> findByCompanyId(Long companyId);
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    Optional<Category> findByNameAndCompanyId(String name, Long companyId);
    List<Category> findByStatus(String status);
    List<Category> findByCompanyIdAndStatus(Long companyId, String status);
    Optional<Category> findByExternalId(Long externalId);
    List<Category> findByCompanyIdAndExternalId(Long companyId, Long parameterId);
    boolean existsByNameAndCompanyId(String name, Long companyId);
    Category save(Category category);
    void deleteById(Long id);
    boolean existsById(Long id);
}