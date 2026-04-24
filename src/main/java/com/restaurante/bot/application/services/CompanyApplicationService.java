package com.restaurante.bot.application.services;

import com.restaurante.bot.application.ports.incoming.CompanyUseCase;
import com.restaurante.bot.application.ports.outgoing.CompanyRepositoryPort;
import com.restaurante.bot.dto.CitySummaryDTO;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.City;
import com.restaurante.bot.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import com.restaurante.bot.util.StatusConstants;
import java.util.Map;

@Service("companyApplicationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyApplicationService implements CompanyUseCase {
    private final CompanyRepositoryPort companyRepo;
    private final CityRepository cityRepository;
    private final com.cloudinary.Cloudinary cloudinary;

    private String uploadLogo(MultipartFile logoFile) throws IOException {
        if (logoFile == null || logoFile.isEmpty()) {
            return null;
        }

        Map uploadResult = cloudinary.uploader().upload(logoFile.getBytes(), com.cloudinary.utils.ObjectUtils.asMap("resource_type", "auto"));
        return (String) uploadResult.get("url");
    }

    @Override
    @Transactional
    public CompanyRequest save(CompanyRequest companyRequest, MultipartFile logoFile) {
        try {
            String logoUrl = uploadLogo(logoFile);

            Company company = new Company();
            company.setName(companyRequest.getNameCompany());
            company.setLongitude(companyRequest.getLongitude());
            company.setLatitude(companyRequest.getLatitude());
            company.setBaseValue(companyRequest.getBaseValue());
            company.setAdditionalValue(companyRequest.getAdditionalValue());
            company.setLogo(logoUrl);
            company.setCityId(companyRequest.getCityId());
            company.setExternalCompanyId(companyRequest.getExternalCompanyId());
            company.setApiKey(companyRequest.getApiKey());
            company.setRpIntegrationId(companyRequest.getRpIntegrationId());
            company.setStatus(StatusConstants.ACTIVE_STATUS);

            Company savedCompany = companyRepo.save(company);

                CompanyRequest response = CompanyRequest.builder()
                    .companyId(savedCompany.getId())
                    .nameCompany(companyRequest.getNameCompany())
                    .logoUrl(savedCompany.getLogo())
                    .longitude(companyRequest.getLongitude())
                    .latitude(companyRequest.getLatitude())
                    .baseValue(companyRequest.getBaseValue())
                    .additionalValue(companyRequest.getAdditionalValue())
                    .externalCompanyId(companyRequest.getExternalCompanyId())
                    .cityId(companyRequest.getCityId())
                    .apiKey(companyRequest.getApiKey())
                    .rpIntegrationId(companyRequest.getRpIntegrationId())
                    .landingTemplate(companyRequest.getLandingTemplate())
                    .build();

                return response;
        } catch (IOException e) {
            throw new DomainException(DomainErrorCode.INTERNAL_ERROR, "Error al subir la imagen del logo");
        }
    }

    @Override
    public List<CompanyRequest> getAllCompany() {
        return companyRepo.getAllCompany();
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        Company company = companyRepo.findById(id)
                .filter(this::isNotDeleted)
                .orElseThrow(() -> new DomainException(DomainErrorCode.NOT_FOUND, "La compañia no fue encontrada por el id " + id));
        company.setStatus(StatusConstants.DELETED_STATUS);
        companyRepo.save(company);
        return true;
    }

    @Override
    @Transactional
    public CompanyRequest update(CompanyRequest companyRequest, MultipartFile logoFile) {
        Company company = companyRepo.findById(companyRequest.getCompanyId())
            .filter(this::isNotDeleted)
            .orElseThrow(() -> new DomainException(DomainErrorCode.NOT_FOUND, "Empresa con ID " + companyRequest.getCompanyId() + " no existe"));

        // update fields if present
        if (companyRequest.getNameCompany() != null) {
            company.setName(companyRequest.getNameCompany());
        }
        if (companyRequest.getLongitude() != null) {
            company.setLongitude(companyRequest.getLongitude());
        }
        if (companyRequest.getLatitude() != null) {
            company.setLatitude(companyRequest.getLatitude());
        }
        if (companyRequest.getBaseValue() != null) {
            company.setBaseValue(companyRequest.getBaseValue());
        }
        if (companyRequest.getAdditionalValue() != null) {
            company.setAdditionalValue(companyRequest.getAdditionalValue());
        }
        if (companyRequest.getExternalCompanyId() != null) {
            company.setExternalCompanyId(companyRequest.getExternalCompanyId());
        }
        if (companyRequest.getCityId() != null) {
            company.setCityId(companyRequest.getCityId());
        }
        if (companyRequest.getApiKey() != null) {
            company.setApiKey(companyRequest.getApiKey());
        }
        if (companyRequest.getRpIntegrationId() != null) {
            company.setRpIntegrationId(companyRequest.getRpIntegrationId());
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String logoUrl = uploadLogo(logoFile);
                company.setLogo(logoUrl);
            } catch (IOException e) {
                throw new DomainException(DomainErrorCode.INTERNAL_ERROR, "Error al subir la imagen del logo");
            }
        }

        company.setUpdatedAt(LocalDateTime.now());
        Company updatedCompany = companyRepo.save(company);

        CompanyRequest response = CompanyRequest.builder()
            .companyId(updatedCompany.getId())
            .nameCompany(companyRequest.getNameCompany())
            .logoUrl(updatedCompany.getLogo())
            .longitude(companyRequest.getLongitude())
            .latitude(companyRequest.getLatitude())
            .baseValue(companyRequest.getBaseValue())
            .additionalValue(companyRequest.getAdditionalValue())
            .externalCompanyId(companyRequest.getExternalCompanyId())
            .cityId(companyRequest.getCityId())
            .apiKey(companyRequest.getApiKey())
            .rpIntegrationId(companyRequest.getRpIntegrationId())
            .landingTemplate(companyRequest.getLandingTemplate())
            .build();

        return response;
    }

    @Override
    public Page<CompanyResponseDTO> getAllPageCompany(int page, int size, String orders, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(orders);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pagingSort = PageRequest.of(page, size, sort);
        return companyRepo.getAllPageCompany(pagingSort);
    }

    @Override
    public CompanyRequest get(Long id) {
        Company company = companyRepo.findById(id)
            .filter(this::isNotDeleted)
            .orElseThrow(() -> new DomainException(DomainErrorCode.NOT_FOUND, "Empresa no encontrada con id " + id));
        return CompanyRequest.builder()
                .companyId(company.getId())
                .nameCompany(company.getName())
                .logoUrl(company.getLogo())
                .longitude(company.getLongitude())
                .latitude(company.getLatitude())
                .baseValue(company.getBaseValue())
                .additionalValue(company.getAdditionalValue())
                .externalCompanyId(company.getExternalCompanyId())
                .city(mapCityResponse(company.getCityId()))
                .apiKey(company.getApiKey())
                .rpIntegrationId(company.getRpIntegrationId())
                .landingTemplate(company.getLandingTemplate())
                .build();
    }

    @Override
    public Page<CompanyResponseDTO> getAll(Map<String, String> customQuery) {
        int page = Integer.parseInt(customQuery.getOrDefault("page", "0"));
        int size = Integer.parseInt(customQuery.getOrDefault("size", "10"));
        String orders = customQuery.getOrDefault("orders", com.restaurante.bot.util.SortConstants.ASC);
        String sortBy = customQuery.getOrDefault("sortBy", "id");
        return getAllPageCompany(page, size, orders, sortBy);
    }

    @Override
    public List<CompanyResponseDTO> getAllWithoutPage(Map<String, String> customQuery) {
        List<CompanyRequest> companies = companyRepo.getAllCompany();
        return companies.stream().map(c -> CompanyResponseDTO.builder()
                .id(c.getCompanyId())
                .companyName(c.getNameCompany())
                .logo(c.getLogoUrl())
                .latitude(c.getLatitude())
                .longitude(c.getLongitude())
                .baseValue(c.getBaseValue())
                .additionalValue(c.getAdditionalValue())
                .externalId(c.getExternalCompanyId())
                .city(c.getCity())
                .apiKey(c.getApiKey())
                .rappyId(c.getRpIntegrationId())
                .landingTemplate(c.getLandingTemplate())
                .build()).toList();
    }

    @Override
    public Page<CompanyResponseDTO> searchCustom(Map<String, String> customQuery) {
        // Basic search behaves like getAll for now; can be extended to apply filters
        return getAll(customQuery);
    }

    private CitySummaryDTO mapCityResponse(Long cityId) {
        if (cityId == null) {
            return null;
        }
        return cityRepository.findById(cityId)
                .map(this::toCityResponse)
                .orElse(null);
    }

    private CitySummaryDTO toCityResponse(City city) {
        return CitySummaryDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }

    private boolean isNotDeleted(Company company) {
        return company == null || company.getStatus() == null
                || !StatusConstants.DELETED_STATUS.equalsIgnoreCase(company.getStatus());
    }
}