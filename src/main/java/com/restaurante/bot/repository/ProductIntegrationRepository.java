package com.restaurante.bot.repository;

import com.restaurante.bot.model.ProductIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductIntegrationRepository extends JpaRepository<ProductIntegration, Long> {

    Optional<ProductIntegration> findByArqProductIdAndCompanyId(Integer arqProductId, Long companyId);
}
