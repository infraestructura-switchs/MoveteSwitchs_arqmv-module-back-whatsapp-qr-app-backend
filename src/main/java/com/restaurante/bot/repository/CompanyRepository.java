package com.restaurante.bot.repository;

import com.restaurante.bot.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

        // Return entities; mapping to DTOs happens in adapter/service layer
        List<Company> findByStatus(String status);

        Page<Company> findByStatus(String status, Pageable pageable);

        Boolean existsByExternalCompanyId(Long externalCompanyId);

        Long countByExternalCompanyId(Long externalCompanyId);

        Company findByExternalCompanyId(Long externalCompanyId);


        Company findFirstByExternalCompanyIdOrderByIdAsc(Long externalCompanyId);

        Company findFirstByIdOrderById(Long companyId);

        @Query("SELECT c.externalCompanyId FROM Company c WHERE c.status = 'ACTIVE'")
        List<Long> findCompanyIds();

}
