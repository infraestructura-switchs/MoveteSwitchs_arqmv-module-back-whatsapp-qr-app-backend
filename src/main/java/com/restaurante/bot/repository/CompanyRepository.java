package com.restaurante.bot.repository;

import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import com.restaurante.bot.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

        @Query(value = "SELECT new com.restaurante.bot.dto.CompanyRequest(c.id, c.name, c.logo, c.numberWhatsapp," +
                        " c.longitude, c.latitude, c.baseValue, c.additionalValue, c.externalCompanyId, c.cityId, c.apiKey, c.rpIntegrationId,c.numberId, c.tokenMeta, c.numberBotMesa, c.numberBotDelivery) "
                        +
                        "FROM Company c " +
                        "WHERE c.status = 'ACTIVE'", countQuery = "SELECT COUNT(*) " +
                                        "FROM Company c " +
                                        "WHERE c.status = 'ACTIVE'")
        List<CompanyRequest> getAllCompany();

        @Query(value = "SELECT new com.restaurante.bot.dto.CompanyResponseDTO(c.id, c.name, c.logo, c.numberWhatsapp," +
                        " c.latitude, c.longitude, c.baseValue, c.additionalValue, c.status, c.externalCompanyId, c.cityId, ci.name,c.apiKey, c.rpIntegrationId,c.numberId, c.tokenMeta,c.numberBotDelivery, c.numberBotMesa, c.statusIntegrationRp,c.tokenMetaDelivery) "
                        +
                        "FROM Company c " +
                        "JOIN City ci ON c.cityId = ci.id " +
                        "WHERE c.status = 'ACTIVE' ")
        Page<CompanyResponseDTO> getAllPageCompany(Pageable pageable);

        Boolean existsByExternalCompanyId(Long externalCompanyId);

        Company findByExternalCompanyId(Long externalCompanyId);

        @Query(value = "SELECT c.external_company_id FROM company c WHERE c.status = 'ACTIVE' ", nativeQuery = true)
        List<Long> findCompanyIds();

}
