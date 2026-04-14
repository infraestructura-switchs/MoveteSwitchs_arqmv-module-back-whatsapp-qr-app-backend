package com.restaurante.bot.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.model.Category;
import com.restaurante.bot.model.CategoryMapping;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.ProductIntegration;
import com.restaurante.bot.repository.CategoryMappingRepository;
import com.restaurante.bot.repository.CategoryRepository;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.ProductIntegrationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.restaurante.bot.util.StatusConstants;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductIntegrationSyncService {

    private final CallServiceHttp callServiceHttp;
    private final CompanyRepository companyRepository;
    private final ProductIntegrationRepository productIntegrationRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMappingRepository categoryMappingRepository;
    private final ObjectMapper objectMapper;

    private final Map<Long, Map<String, Long>> dynamicCategoryMapping = new HashMap<>();

    @Transactional
    public GenericResponse syncAllCompanies() {
        log.info("Iniciando sincronizacion de ProductIntegration para todas las companias");

        List<Long> companyIdsToProcess = companyRepository.findCompanyIds();
        int totalIntegrationsUpdated = 0;
        int companiesProcessed = 0;

        for (Long companyId : companyIdsToProcess) {
            int syncedByCompany = syncCompanyInternal(companyId);
            if (syncedByCompany > 0) {
                companiesProcessed++;
                totalIntegrationsUpdated += syncedByCompany;
            }
        }

        String message = String.format("Sincronizacion ProductIntegration completada: %d companias procesadas, %d integraciones actualizadas", companiesProcessed, totalIntegrationsUpdated);
        log.info(message);
        return new GenericResponse(message, 200L);
    }

    @Transactional
    public GenericResponse syncCompany(Long externalCompanyId) {
        int total = syncCompanyInternal(externalCompanyId);
        if (total == 0) {
            return new GenericResponse("No se encontraron productos para sincronizar", 200L);
        }
        return new GenericResponse(String.format("Sincronizacion ProductIntegration completada para compania %d: %d integraciones actualizadas", externalCompanyId, total), 200L);
    }

    private int syncCompanyInternal(Long externalCompanyId) {
        loadCategoryMapping(externalCompanyId);

        List<ProductDTO> products = callServiceHttp.getProduct(externalCompanyId);
        if (products == null || products.isEmpty()) {
            log.warn("No se encontraron productos para la compania {}, se omite.", externalCompanyId);
            return 0;
        }

        int syncedCount = 0;
        for (ProductDTO productDTO : products) {
            ProductIntegration integration = mapToProductIntegration(productDTO, externalCompanyId);
            productIntegrationRepository.save(integration);
            syncedCount++;
        }

        return syncedCount;
    }

    private ProductIntegration mapToProductIntegration(ProductDTO productDTO, Long externalCompanyId) {
        Integer arqProductId = Math.toIntExact(productDTO.getId());
        ProductIntegration integration = productIntegrationRepository
            .findByArqProductIdAndCompanyId(arqProductId, externalCompanyId)
            .orElse(new ProductIntegration());

        Long categoryId = getOrCreateCategory(productDTO, externalCompanyId);

        integration.setArqProductId(arqProductId);
        integration.setName(productDTO.getData().getDescripcion());
        integration.setPrice(productDTO.getData().getPrecio());
        integration.setOriginalPrice(productDTO.getData().getPrecioSinImpuestos() != null
            ? productDTO.getData().getPrecioSinImpuestos()
            : productDTO.getData().getPrecio());
        integration.setDescription(productDTO.getData().getDescripcion());
        integration.setGroupId(parseGroupId(productDTO));
        integration.setCompanyId(externalCompanyId);
        integration.setStatus(StatusConstants.ACTIVE_STATUS);
        integration.setImgProduct(productDTO.getData().getImagenMenu());
        integration.setCategoryId(categoryId);
        integration.setComments(serializeComments(productDTO));
        integration.setInformation(productDTO.getData().getInformacion());
        integration.setPreparationTime(productDTO.getData().getMinutosPreparacion() != null
            ? Math.toIntExact(Math.round(productDTO.getData().getMinutosPreparacion()))
            : 0);

        return integration;
    }

    private String serializeComments(ProductDTO productDTO) {
        try {
            if (productDTO.getData().getComentarios() == null) {
                return null;
            }
            return objectMapper.writeValueAsString(productDTO.getData().getComentarios());
        } catch (JsonProcessingException e) {
            log.error("Error serializando comentarios para arqProductId {}: {}", productDTO.getId(), e.getMessage());
            return "[]";
        }
    }

    private Long parseGroupId(ProductDTO productDTO) {
        try {
            String groupId = productDTO.getData().getGrupo().getIdGrupo();
            return groupId == null ? null : Long.parseLong(groupId);
        } catch (NumberFormatException e) {
            log.warn("No se pudo parsear grupo para arqProductId {}", productDTO.getId());
            return null;
        }
    }

    private void loadCategoryMapping(Long externalCompanyId) {
        dynamicCategoryMapping.computeIfAbsent(externalCompanyId, k -> {
            Map<String, Long> mapping = new HashMap<>();
            List<CategoryMapping> mappings = categoryMappingRepository.findByCompanyIdAndStatus(externalCompanyId, StatusConstants.ACTIVE_STATUS);
            for (CategoryMapping cm : mappings) {
                mapping.put(cm.getGroupId().toString(), cm.getCategoryId());
            }
            return mapping;
        });
    }

    private Long getOrCreateCategory(ProductDTO productDTO, Long externalCompanyId) {
        String groupId = productDTO.getData().getGrupo().getIdGrupo();
        String groupDescription = productDTO.getData().getGrupo().getDescripcion().toUpperCase();

        Map<String, Long> companyMapping = dynamicCategoryMapping.computeIfAbsent(externalCompanyId, k -> new HashMap<>());
        Long categoryId = companyMapping.get(groupId);

        if (categoryId == null) {
            categoryId = companyMapping.get(groupDescription);
        }

        if (categoryId == null) {
            Category category = new Category();
            category.setName(groupDescription);
            category.setExternalId(Long.parseLong(groupId));
            category.setStatus(StatusConstants.ACTIVE_STATUS);
            category.setCompanyId(externalCompanyId);
            category = categoryRepository.save(category);
            categoryId = category.getCategoryId();

            companyMapping.put(groupId, categoryId);
            companyMapping.put(groupDescription, categoryId);

            CategoryMapping mapping = new CategoryMapping();
            mapping.setGroupId(Long.parseLong(groupId));
            mapping.setCategoryId(categoryId);
            mapping.setCompanyId(externalCompanyId);
            mapping.setStatus(StatusConstants.ACTIVE_STATUS);
            categoryMappingRepository.save(mapping);
            log.info("Nueva categoria creada para groupId {}: {}", groupId, categoryId);
        }

        return categoryId;
    }
}
