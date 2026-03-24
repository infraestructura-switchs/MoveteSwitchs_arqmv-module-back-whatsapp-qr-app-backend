package com.restaurante.bot.business.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.restaurante.bot.business.interfaces.CompanyInterface;
import com.restaurante.bot.dto.CityResponseDTO;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.City;
import com.restaurante.bot.repository.CityRepository;
import com.restaurante.bot.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService implements CompanyInterface {

    private final CompanyRepository companyRepository;
    private final CityRepository cityRepository;
    private final Cloudinary cloudinary;

    private String uploadLogo(MultipartFile logoFile) throws IOException {
        if (logoFile == null || logoFile.isEmpty()) {
            return null;
        }

        Map uploadResult = cloudinary.uploader().upload(logoFile.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        return (String) uploadResult.get("url");
    }

    @Override
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
            company.setStatus("ACTIVE");

            Company savedCompany = companyRepository.save(company);

            companyRequest.setCompanyId(savedCompany.getId());
            companyRequest.setLogoUrl(savedCompany.getLogo());

            return companyRequest;

        } catch (IOException e) {
            log.error("Error al subir la imagen del logo", e);
            throw new GenericException("Error al subir la imagen del logo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<CompanyRequest> getAllCompany() {
        return companyRepository.findByStatus("ACTIVE").stream()
            .map(company -> CompanyRequest.builder()
                .companyId(company.getId())
                .nameCompany(company.getName())
                .logoUrl(company.getLogo())
                .longitude(company.getLongitude())
                .latitude(company.getLatitude())
                .baseValue(company.getBaseValue())
                .additionalValue(company.getAdditionalValue())
                .externalCompanyId(company.getExternalCompanyId())
                .cityId(company.getCityId())
                .city(mapCityResponse(company.getCityId()))
                .apiKey(company.getApiKey())
                .rpIntegrationId(company.getRpIntegrationId())
                .landingTemplate(company.getLandingTemplate())
                .status(company.getStatus())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public Boolean delete(Long id) {
        if (companyRepository.existsById(id)) {
            Company company = companyRepository.findById(id).get();
            company.setStatus("INACTIVE");
            companyRepository.save(company);
            return true;
        } else {
            throw new GenericException("La compañia no fue encontrada por el id " + id, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public CompanyRequest update(CompanyRequest companyRequest, MultipartFile logoFile) {
        log.info("Actualizando empresa con ID: {}", companyRequest.getCompanyId());

        Company company = companyRepository.findById(companyRequest.getCompanyId())
            .orElseThrow(() -> new GenericException("Empresa con ID " + companyRequest.getCompanyId() + " no existe", HttpStatus.NOT_FOUND));

        // Actualizar solo si el valor no es null
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
                log.error("Error al subir la imagen del logo", e);
                throw new GenericException("Error al subir la imagen del logo", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // Actualizar timestamp
        company.setUpdatedAt(LocalDateTime.now());

        Company updatedCompany = companyRepository.save(company);

        // Actualizar el DTO con los datos devueltos
        companyRequest.setCompanyId(updatedCompany.getId());
        companyRequest.setLogoUrl(updatedCompany.getLogo());

        return companyRequest;
    }

    @Override
    public Page<CompanyResponseDTO> getAllPageCompany(int page, int size, String orders, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(orders);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pagingSort = PageRequest.of(page, size, sort);
        Page<Company> companies = companyRepository.findByStatus("ACTIVE", pagingSort);
        List<CompanyResponseDTO> content = companies.getContent().stream().map(company -> CompanyResponseDTO.builder()
                .id(company.getId())
                .companyName(company.getName())
                .logo(company.getLogo())
                .latitude(company.getLatitude())
                .longitude(company.getLongitude())
                .baseValue(company.getBaseValue())
                .additionalValue(company.getAdditionalValue())
                .status(company.getStatus())
                .externalId(company.getExternalCompanyId())
                .city(mapCityResponse(company.getCityId()))
                .apiKey(company.getApiKey())
                .rappyId(company.getRpIntegrationId())
                .landingTemplate(company.getLandingTemplate())
                .build()).collect(Collectors.toList());

        return new PageImpl<>(content, pagingSort, companies.getTotalElements());

    }

    private CityResponseDTO mapCityResponse(Long cityId) {
        if (cityId == null) {
            return null;
        }
        return cityRepository.findById(cityId)
                .map(this::toCityResponse)
                .orElse(null);
    }

    private CityResponseDTO toCityResponse(City city) {
        return CityResponseDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }

}
