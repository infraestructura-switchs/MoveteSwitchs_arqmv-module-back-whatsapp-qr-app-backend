package com.restaurante.bot.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.api.dto.GroupDTO;
import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.api.dto.ProductDataDTO;
import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.model.Category;
import com.restaurante.bot.model.CategoryMapping;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.model.ProductIntegration;
import com.restaurante.bot.repository.CategoryIntegrationRepository;
import com.restaurante.bot.repository.CategoryMappingRepository;
import com.restaurante.bot.repository.CategoryRepository;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.ProductIntegrationRepository;
import com.restaurante.bot.repository.ProductRepository;
import com.restaurante.bot.util.StatusConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductIntegrationSyncServiceTest {

    @Mock
    private CallServiceHttp callServiceHttp;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ProductIntegrationRepository productIntegrationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMappingRepository categoryMappingRepository;

    @Mock
    private CategoryIntegrationRepository categoryIntegrationRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductIntegrationSyncService service;

    @Test
    void syncCompany_ShouldKeepExistingIntegrationId_AndMapSoftRestaurantId() {
        Long externalCompanyId = 889L;
        Long internalCompanyId = 10L;

        ProductDTO dto = buildProductDto(101L, "SR-101", "11", 15);
        Company company = new Company();
        company.setId(internalCompanyId);
        company.setExternalCompanyId(externalCompanyId);

        ProductIntegration existing = ProductIntegration.builder()
            .productIntegrationId(999L)
            .arqProductId(101)
            .companyId(externalCompanyId)
            .name("OLD")
            .build();

        CategoryMapping mapping = new CategoryMapping();
        mapping.setGroupId(11L);
        mapping.setCategoryId(500L);

        Category category = new Category();
        category.setCategoryId(77L);
        category.setCompanyId(internalCompanyId);
        category.setExternalId(500L);

        when(categoryMappingRepository.findByCompanyIdAndStatus(externalCompanyId, StatusConstants.ACTIVE_STATUS))
            .thenReturn(List.of(mapping));
        when(callServiceHttp.getProduct(externalCompanyId)).thenReturn(List.of(dto));
        when(companyRepository.findByExternalCompanyId(externalCompanyId)).thenReturn(company);
        when(productIntegrationRepository.findByArqProductIdAndCompanyId(101, externalCompanyId))
            .thenReturn(Optional.of(existing));
        when(categoryRepository.findByCompanyIdAndExternalId(internalCompanyId, 500L))
            .thenReturn(List.of(category));
        when(productIntegrationRepository.save(any(ProductIntegration.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findByProductIntegration_ProductIntegrationId(999L))
            .thenReturn(Optional.of(new Product()));

        GenericResponse response = service.syncCompany(externalCompanyId);

        ArgumentCaptor<ProductIntegration> integrationCaptor = ArgumentCaptor.forClass(ProductIntegration.class);
        verify(productIntegrationRepository).save(integrationCaptor.capture());
        ProductIntegration saved = integrationCaptor.getValue();

        assertEquals(999L, saved.getProductIntegrationId());
        assertEquals("SR-101", saved.getSoftRestaurantId());
        assertEquals("Producto 101", saved.getName());
        assertEquals(77L, saved.getCategoryId());
        assertEquals(StatusConstants.ACTIVE_STATUS, saved.getStatus());
        assertNotNull(response);
    }

    @Test
    void syncCompany_ShouldCreateProductWithIntegration_WhenNoExistingProductLinked() {
        Long externalCompanyId = 889L;
        Long internalCompanyId = 10L;

        ProductDTO dto = buildProductDto(202L, "SR-202", "12", 8);
        Company company = new Company();
        company.setId(internalCompanyId);
        company.setExternalCompanyId(externalCompanyId);

        CategoryMapping mapping = new CategoryMapping();
        mapping.setGroupId(12L);
        mapping.setCategoryId(700L);

        Category category = new Category();
        category.setCategoryId(88L);
        category.setCompanyId(internalCompanyId);
        category.setExternalId(700L);

        when(categoryMappingRepository.findByCompanyIdAndStatus(externalCompanyId, StatusConstants.ACTIVE_STATUS))
            .thenReturn(List.of(mapping));
        when(callServiceHttp.getProduct(externalCompanyId)).thenReturn(List.of(dto));
        when(companyRepository.findByExternalCompanyId(externalCompanyId)).thenReturn(company);
        when(productIntegrationRepository.findByArqProductIdAndCompanyId(202, externalCompanyId))
            .thenReturn(Optional.empty());
        when(categoryRepository.findByCompanyIdAndExternalId(internalCompanyId, 700L))
            .thenReturn(List.of(category));
        when(productIntegrationRepository.save(any(ProductIntegration.class))).thenAnswer(invocation -> {
            ProductIntegration integration = invocation.getArgument(0);
            integration.setProductIntegrationId(1234L);
            return integration;
        });
        when(productRepository.findByProductIntegration_ProductIntegrationId(1234L))
            .thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.syncCompany(externalCompanyId);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertEquals(202L, savedProduct.getProductId());
        assertEquals(internalCompanyId, savedProduct.getCompanyId());
        assertNotNull(savedProduct.getProductIntegration());
        assertEquals("SR-202", savedProduct.getProductIntegration().getSoftRestaurantId());
        assertEquals(1234L, savedProduct.getProductIntegration().getProductIntegrationId());
        assertEquals(88L, savedProduct.getCategoryId());
    }

    private ProductDTO buildProductDto(Long id, String softRestaurantId, String groupId, Integer prepMinutes) {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setIdGrupo(groupId);
        groupDTO.setDescripcion("Bebidas");

        ProductDataDTO dataDTO = new ProductDataDTO();
        dataDTO.setDescripcion("Producto " + id);
        dataDTO.setPrecio(25000.0);
        dataDTO.setPrecioSinImpuestos(20000.0);
        dataDTO.setGrupo(groupDTO);
        dataDTO.setImagenMenu("img.png");
        dataDTO.setComentarios(Collections.emptyList());
        dataDTO.setInformacion("Info");
        dataDTO.setMinutosPreparacion(prepMinutes);

        ProductDTO dto = new ProductDTO();
        dto.setId(id);
        dto.setIdProducto(softRestaurantId);
        dto.setData(dataDTO);
        return dto;
    }
}
