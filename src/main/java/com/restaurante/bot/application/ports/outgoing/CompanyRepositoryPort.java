package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CompanyRepositoryPort {
    Company save(Company company);
    Optional<Company> findById(Long id);
    boolean existsById(Long id);
    List<com.restaurante.bot.dto.CompanyRequest> getAllCompany();
    org.springframework.data.domain.Page<com.restaurante.bot.dto.CompanyResponseDTO> getAllPageCompany(Pageable pageable);
    // additional query methods used by service (if any)
}