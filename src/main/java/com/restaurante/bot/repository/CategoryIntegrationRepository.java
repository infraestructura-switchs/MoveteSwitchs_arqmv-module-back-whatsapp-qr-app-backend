package com.restaurante.bot.repository;

import com.restaurante.bot.model.CategoryIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryIntegrationRepository extends JpaRepository<CategoryIntegration, Long> {
    
    Optional<CategoryIntegration> findByExternalId(Long externalId);
    
    Optional<CategoryIntegration> findByExternalIdAndCompanyId(Long externalId, Long companyId);
    
}
