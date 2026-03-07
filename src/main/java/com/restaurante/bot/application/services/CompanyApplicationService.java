package com.restaurante.bot.application.services;

import com.restaurante.bot.application.ports.incoming.CompanyUseCase;
import com.restaurante.bot.application.ports.outgoing.CompanyRepositoryPort;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import com.restaurante.bot.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service("companyApplicationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyApplicationService implements CompanyUseCase {
    private final CompanyRepositoryPort companyRepo;
    private final com.cloudinary.Cloudinary cloudinary;

    @Override
    @Transactional
    public CompanyRequest save(CompanyRequest companyRequest, MultipartFile logoFile) {
        try {
            Map uploadResult = cloudinary.uploader().upload(logoFile.getBytes(), com.cloudinary.utils.ObjectUtils.asMap("resource_type", "auto"));
            String logoUrl = (String) uploadResult.get("url");

            Company company = new Company();
            company.setName(companyRequest.getNameCompany());
            company.setNumberWhatsapp(companyRequest.getNumberWhatsapp());
            company.setLongitude(companyRequest.getLongitude());
            company.setLatitude(companyRequest.getLatitude());
            company.setBaseValue(companyRequest.getBaseValue());
            company.setAdditionalValue(companyRequest.getAdditionalValue());
            company.setLogo(logoUrl);
            company.setCityId(companyRequest.getCityId());
            company.setExternalCompanyId(companyRequest.getExternalCompanyId());
            company.setApiKey(companyRequest.getApiKey());
            company.setRpIntegrationId(companyRequest.getRpIntegrationId());
            company.setNumberId(companyRequest.getNumberId());
            company.setTokenMeta(companyRequest.getTokenMeta());
            company.setNumberBotMesa(companyRequest.getNumberBotMesa());
            company.setNumberBotDelivery(companyRequest.getNumberBotDelivery());
            company.setTokenMetaDelivery(companyRequest.getTokenMetaDelivery());
            company.setStatus("ACTIVE");

            Company savedCompany = companyRepo.save(company);

            companyRequest.setCompanyId(savedCompany.getId());
            companyRequest.setLogoUrl(savedCompany.getLogo());

            return companyRequest;
        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen del logo", e);
        }
    }

    @Override
    public List<CompanyRequest> getAllCompany() {
        return companyRepo.getAllCompany();
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        if (companyRepo.existsById(id)) {
            Company company = companyRepo.findById(id).orElseThrow();
            company.setStatus("INACTIVE");
            companyRepo.save(company);
            return true;
        } else {
            throw new RuntimeException("La compañia no fue encontrada por el id " + id);
        }
    }

    @Override
    @Transactional
    public CompanyRequest update(CompanyRequest companyRequest, MultipartFile logoFile) {
        Company company = companyRepo.findById(companyRequest.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Empresa con ID " + companyRequest.getCompanyId() + " no existe"));

        // update fields if present
        if (companyRequest.getNameCompany() != null) {
            company.setName(companyRequest.getNameCompany());
        }
        if (companyRequest.getNumberWhatsapp() != null) {
            company.setNumberWhatsapp(companyRequest.getNumberWhatsapp());
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
        if (companyRequest.getNumberId() != null) {
            company.setNumberId(companyRequest.getNumberId());
        }
        if (companyRequest.getTokenMeta() != null) {
            company.setTokenMeta(companyRequest.getTokenMeta());
        }
        if (companyRequest.getNumberBotMesa() != null) {
            company.setNumberBotMesa(companyRequest.getNumberBotMesa());
        }
        if (companyRequest.getNumberBotDelivery() != null) {
            company.setNumberBotDelivery(companyRequest.getNumberBotDelivery());
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(logoFile.getBytes(), com.cloudinary.utils.ObjectUtils.asMap("resource_type", "auto"));
                String logoUrl = (String) uploadResult.get("url");
                company.setLogo(logoUrl);
            } catch (IOException e) {
                throw new RuntimeException("Error al subir la imagen del logo", e);
            }
        }

        company.setUpdatedAt(LocalDateTime.now());
        Company updatedCompany = companyRepo.save(company);
        companyRequest.setCompanyId(updatedCompany.getId());
        companyRequest.setLogoUrl(updatedCompany.getLogo());

        return companyRequest;
    }

    @Override
    public Page<CompanyResponseDTO> getAllPageCompany(int page, int size, String orders, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(orders);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pagingSort = PageRequest.of(page, size, sort);
        return companyRepo.getAllPageCompany(pagingSort);
    }
}