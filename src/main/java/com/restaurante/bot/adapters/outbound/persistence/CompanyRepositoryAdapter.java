package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.CompanyRepositoryPort;
import com.restaurante.bot.dto.CityResponseDTO;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.City;
import com.restaurante.bot.repository.CityRepository;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {
    private final CompanyRepository companyRepository;
    private final CityRepository cityRepository;

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
        return companies.stream().map(c -> {
            CompanyRequest dto = new CompanyRequest();
            dto.setCompanyId(c.getId());
            dto.setNameCompany(c.getName());
            dto.setLogoUrl(c.getLogo());
            dto.setLongitude(c.getLongitude());
            dto.setLatitude(c.getLatitude());
            dto.setBaseValue(c.getBaseValue());
            dto.setAdditionalValue(c.getAdditionalValue());
            dto.setExternalCompanyId(c.getExternalCompanyId());
            dto.setCityId(c.getCityId());
            dto.setCity(mapCityResponse(c.getCityId() == null ? null : cityRepository.findById(c.getCityId()).orElse(null)));
            dto.setApiKey(c.getApiKey());
            dto.setRpIntegrationId(c.getRpIntegrationId());
            dto.setLandingTemplate(c.getLandingTemplate());
            dto.setStatus(c.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<CompanyResponseDTO> getAllPageCompany(Pageable pageable) {
        Page<Company> page = companyRepository.findByStatus(Constants.ACTIVE_STATUS, pageable);
        Set<Long> cityIds = page.getContent().stream()
                .map(Company::getCityId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        final Map<Long, City> citiesById = cityIds.isEmpty()
            ? new HashMap<>()
            : cityRepository.findAllById(cityIds).stream()
                .collect(Collectors.toMap(City::getId, city -> city));

        List<CompanyResponseDTO> content = page.getContent().stream().map(c -> {
            CompanyResponseDTO dto = new CompanyResponseDTO();
            dto.setId(c.getId());
            dto.setCompanyName(c.getName());
            dto.setLogo(c.getLogo());
            dto.setLatitude(c.getLatitude());
            dto.setLongitude(c.getLongitude());
            dto.setBaseValue(c.getBaseValue());
            dto.setAdditionalValue(c.getAdditionalValue());
            dto.setStatus(c.getStatus());
            dto.setExternalId(c.getExternalCompanyId());
            dto.setCityId(c.getCityId());
            dto.setApiKey(c.getApiKey());
            dto.setRappyId(c.getRpIntegrationId());
            dto.setLandingTemplate(c.getLandingTemplate());
            dto.setCity(mapCityResponse(citiesById.get(c.getCityId())));
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    private CityResponseDTO mapCityResponse(City city) {
        if (city == null) {
            return null;
        }
        return CityResponseDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .status(city.getStatus())
                .createdAt(city.getCreatedAt())
                .updatedAt(city.getUpdatedAt())
                .build();
    }
}
