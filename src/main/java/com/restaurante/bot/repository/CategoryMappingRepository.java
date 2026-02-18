package com.restaurante.bot.repository;

import com.restaurante.bot.model.CategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryMappingRepository extends JpaRepository<CategoryMapping, Long> {

    List<CategoryMapping> findByCompanyIdAndStatus(Long companyId, String status);

    Optional<CategoryMapping> findByGroupIdAndCompanyIdAndStatus(Long groupId, Long companyId, String status);
}
