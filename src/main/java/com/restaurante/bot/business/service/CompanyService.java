package com.restaurante.bot.business.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.restaurante.bot.business.interfaces.CompanyInterface;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CompanyService implements CompanyInterface {

    private final CompanyRepository companyRepository;
    private final Cloudinary cloudinary;

    @Override
    public CompanyRequest save(CompanyRequest companyRequest, MultipartFile logoFile) {
        try {
            Map uploadResult = cloudinary.uploader().upload(logoFile.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
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

            Company savedCompany = companyRepository.save(company);

            companyRequest.setCompanyId(savedCompany.getId());
            companyRequest.setLogoUrl(savedCompany.getLogo());

            return companyRequest;

        } catch (IOException e) {
            log.error("Error al subir la imagen del logo", e);
            throw new RuntimeException("Error al subir la imagen del logo", e);
        }
    }

    @Override
    public List<CompanyRequest> getAllCompany() {
        return companyRepository.getAllCompany();
    }

    @Override
    public Boolean delete(Long id) {
        if (companyRepository.existsById(id)) {
            Company company = companyRepository.findById(id).get();
            company.setStatus("INACTIVE");
            companyRepository.save(company);
            return true;
        } else {
            throw new RuntimeException("La compañia no fue encontrada por el id " + id);
        }
    }

    @Override
    @Transactional
    public CompanyRequest update(CompanyRequest companyRequest, MultipartFile logoFile) {
        log.info("Actualizando empresa con ID: {}", companyRequest.getCompanyId());

        Company company = companyRepository.findById(companyRequest.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Empresa con ID " + companyRequest.getCompanyId() + " no existe"));

        // Actualizar solo si el valor no es null
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
                Map uploadResult = cloudinary.uploader().upload(logoFile.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                String logoUrl = (String) uploadResult.get("url");
                company.setLogo(logoUrl);
            } catch (IOException e) {
                log.error("Error al subir la imagen del logo", e);
                throw new RuntimeException("Error al subir la imagen del logo", e);
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
        return companyRepository.getAllPageCompany(pagingSort);

    }

}
