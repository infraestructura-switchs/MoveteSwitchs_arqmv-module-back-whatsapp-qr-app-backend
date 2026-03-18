package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.CompanyRepositoryPort;
import com.restaurante.bot.dto.CityResponseDTO;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {
    private final CompanyRepository companyRepository;

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return companyRepository.existsById(id);
    }

    @Override
    public List<CompanyRequest> getAllCompany() {
        List<Company> companies = companyRepository.findByStatus(Constants.ACTIVE_STATUS);
        return companies.stream().map(c -> CompanyRequest.builder()
                .companyId(c.getId())
                .nameCompany(c.getName())
                .logoUrl(c.getLogo())
                .numberWhatsapp(c.getNumberWhatsapp())
                .longitude(c.getLongitude())
                .latitude(c.getLatitude())
                .baseValue(c.getBaseValue())
                .additionalValue(c.getAdditionalValue())
                .externalCompanyId(c.getExternalCompanyId())
                .cityId(c.getCityId())
                .apiKey(c.getApiKey())
                .rpIntegrationId(c.getRpIntegrationId())
                .numberId(c.getNumberId())
                .tokenMeta(c.getTokenMeta())
                .tokenMetaDelivery(c.getTokenMetaDelivery())
                .numberBotMesa(c.getNumberBotMesa())
                .numberBotDelivery(c.getNumberBotDelivery())
                .landingTemplate(c.getLandingTemplate())
                .status(c.getStatus())
                .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<CompanyResponseDTO> getAllPageCompany(Pageable pageable) {
        Page<Company> page = companyRepository.findByStatus(Constants.ACTIVE_STATUS, pageable);
        List<CompanyResponseDTO> content = page.getContent().stream().map(c -> {
            CompanyResponseDTO dto = CompanyResponseDTO.builder()
                    .id(c.getId())
                    .companyName(c.getName())
                    .logo(c.getLogo())
                    .whatsappNumber(c.getNumberWhatsapp())
                    .latitude(c.getLatitude())
                    .longitude(c.getLongitude())
                    .baseValue(c.getBaseValue())
                    .aditionalValue(c.getAdditionalValue())
                    .status(c.getStatus())
                    .externalId(c.getExternalCompanyId())
                    .cityId(c.getCityId())
                    .apiKey(c.getApiKey())
                    .rappyId(c.getRpIntegrationId())
                    .numberId(c.getNumberId())
                    .tokenMetaQr(c.getTokenMeta())
                    .numberBotDelivery(c.getNumberBotDelivery())
                    .numberBotMesa(c.getNumberBotMesa())
                    .statusRappy(c.getStatusIntegrationRp())
                    .tokenMetaDelivery(c.getTokenMetaDelivery())
                    .landingTemplate(c.getLandingTemplate())
                    .build();

            // If City info is needed, adapt to fetch City entity and map to CityResponseDTO here
            dto.setCity(null);
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }
}
