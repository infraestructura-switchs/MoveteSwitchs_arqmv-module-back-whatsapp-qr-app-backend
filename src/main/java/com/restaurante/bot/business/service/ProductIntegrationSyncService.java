package com.restaurante.bot.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

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
    private final CategoryIntegrationRepository categoryIntegrationRepository;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;

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

        Company company = companyRepository.findByExternalCompanyId(externalCompanyId);

        int syncedCount = 0;
        for (ProductDTO productDTO : products) {
            ProductIntegration integration = mapToProductIntegration(productDTO,company.getId(), externalCompanyId);
            integration = productIntegrationRepository.save(integration);

            Optional<Product> optionalProduct = productRepository.findByProductIntegration_ProductIntegrationId(integration.getProductIntegrationId());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.setName(integration.getName());
                product.setPrice(integration.getPrice());
                productRepository.save(product);
            } else {
                Product newProduct = new Product();
                newProduct.setProductId(productDTO.getId());
                newProduct.setName(integration.getName());
                newProduct.setPrice(integration.getPrice());
                newProduct.setOriginalPrice(integration.getOriginalPrice());
                newProduct.setDescription(integration.getDescription());
                newProduct.setCategoryId(integration.getCategoryId());
                newProduct.setCompanyId(company.getId());
                newProduct.setStatus(StatusConstants.ACTIVE_STATUS);
                newProduct.setImgProduct(integration.getImgProduct());
                newProduct.setComments(integration.getComments());
                newProduct.setInformation(integration.getInformation());
                newProduct.setPreparationTime(integration.getPreparationTime());
                newProduct.setProductIntegration(integration);
                
                productRepository.save(newProduct);
            }

            syncedCount++;
        }

        return syncedCount;
    }

    private ProductIntegration mapToProductIntegration(ProductDTO productDTO,Long companyId,
                                                       Long externalCompanyId) {

        Integer arqProductId = Math.toIntExact(productDTO.getId());
        String softRestaurantId = productDTO.getIdProducto();
        ProductIntegration integration = productIntegrationRepository
            .findByArqProductIdAndCompanyId(arqProductId, externalCompanyId)
            .orElse(new ProductIntegration());

        Long externalCategoryId = getOrCreateCategory(productDTO, companyId, externalCompanyId);

        List<Category> categories = categoryRepository.findByCompanyIdAndExternalId(companyId, externalCategoryId);
        Category category = categories.isEmpty() ? new Category() : categories.get(0);

        return integration.toBuilder()
            .arqProductId(arqProductId)
            .name(productDTO.getData().getDescripcion())
            .price(productDTO.getData().getPrecio())
            .originalPrice(productDTO.getData().getPrecioSinImpuestos() != null
                ? productDTO.getData().getPrecioSinImpuestos()
                : productDTO.getData().getPrecio())
            .description(productDTO.getData().getDescripcion())
            .groupId(parseGroupId(productDTO))
            .companyId(externalCompanyId)
            .status(StatusConstants.ACTIVE_STATUS)
            .imgProduct(productDTO.getData().getImagenMenu())
            .categoryId(category.getCategoryId())
            .comments(serializeComments(productDTO))
            .information(productDTO.getData().getInformacion())
            .preparationTime(productDTO.getData().getMinutosPreparacion() != null
                ? Math.toIntExact(Math.round(productDTO.getData().getMinutosPreparacion()))
                : 0)
            .softRestaurantId(softRestaurantId)
            .build();
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

    private Long getOrCreateCategory(ProductDTO productDTO, Long companyId,
                                     Long externalCompanyId) {
        String groupId = productDTO.getData().getGrupo().getIdGrupo();
        String groupDescription = productDTO.getData().getGrupo().getDescripcion().toUpperCase();

        Map<String, Long> companyMapping = dynamicCategoryMapping.computeIfAbsent(externalCompanyId, k -> new HashMap<>());
        Long categoryIntegrationId = companyMapping.get(groupId);

        if (categoryIntegrationId == null) {
            categoryIntegrationId = companyMapping.get(groupDescription);
        }

        if (categoryIntegrationId == null) {
            CategoryIntegration categoryIntegrationDto = new CategoryIntegration();
            categoryIntegrationDto.setName(groupDescription);
            categoryIntegrationDto.setExternalId(Long.parseLong(groupId));
            categoryIntegrationDto.setStatus(StatusConstants.ACTIVE_STATUS);
            categoryIntegrationDto.setCompanyId(externalCompanyId);
            CategoryIntegration categoryIntegration = categoryIntegrationRepository.save(categoryIntegrationDto);
            categoryIntegrationId = categoryIntegration.getCategoryIntegrationId();

            Category category = new Category();
            category.setName(categoryIntegration.getName());
            category.setExternalId(categoryIntegrationId);
            category.setStatus(categoryIntegration.getStatus());
            category.setCompanyId(companyId);
            categoryRepository.save(category);

            companyMapping.put(groupId, categoryIntegrationId);
            companyMapping.put(groupDescription, categoryIntegrationId);

            CategoryMapping mapping = new CategoryMapping();
            mapping.setGroupId(Long.parseLong(groupId));
            mapping.setCategoryId(categoryIntegrationId);
            mapping.setCompanyId(externalCompanyId);
            mapping.setStatus(StatusConstants.ACTIVE_STATUS);
            categoryMappingRepository.save(mapping);
            log.info("Nueva categoria creada para groupId {}: {}", groupId, categoryIntegrationId);
        }

        return categoryIntegrationId;
    }
}
