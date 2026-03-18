package com.restaurante.bot.business.service;

import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductCategoryDTO;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.model.Category;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.model.ProductDiscount;
import com.restaurante.bot.repository.CategoryRepository;
import com.restaurante.bot.repository.CategoryMappingRepository;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.ParameterRepository;
import com.restaurante.bot.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CallServiceHttp callServiceHttp;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CategoryMappingRepository categoryMappingRepository;

    @Mock
    private ParameterRepository parameterRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProductDiscountSupport productDiscountSupport;

    @InjectMocks
    private ProductService productService;

    private Category bebidasCategory;
    private Product cocaColaProduct;

    @BeforeEach
    void setUp() {
        bebidasCategory = new Category();
        bebidasCategory.setCategoryId(10L);
        bebidasCategory.setName("Bebidas");
        bebidasCategory.setStatus("ACTIVO");
        bebidasCategory.setCompanyId(1L);

        cocaColaProduct = new Product();
        cocaColaProduct.setProductId(1L);
        cocaColaProduct.setName("Coca Cola");
        cocaColaProduct.setPrice(2500.0);
        cocaColaProduct.setDescription("");
        cocaColaProduct.setStatus("ACTIVO");
        cocaColaProduct.setImgProduct("coca-cola.jpg");
        cocaColaProduct.setCategoryId(10L);
        cocaColaProduct.setCompanyId(1L);

        // Configura el defaultProductImage usando ReflectionTestUtils (ya que es @Value)
        ReflectionTestUtils.setField(productService, "defaultProductImage", "default.jpg");

        // Mockea el SecurityContextHolder para simular autenticación JWT
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(1L); // Simula tokenCompanyId = 1L
        when(authentication.isAuthenticated()).thenReturn(true); // important for service check

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getProductsSfotRestaurantByCompanyId_ShouldReturnCategorizedProducts() {
        Long companyId = 1L;
        when(categoryRepository.findByCompanyId(companyId)).thenReturn(Arrays.asList(bebidasCategory));
        when(productRepository.findByCompanyIdOrderByNameAsc(companyId)).thenReturn(Arrays.asList(cocaColaProduct));
        when(companyRepository.existsByExternalCompanyId(companyId)).thenReturn(true); // Mock para la verificación de compañía
        when(productDiscountSupport.findActiveDiscountsByProductIds(eq(companyId), anyList())).thenReturn(java.util.Collections.emptyMap());
        when(productDiscountSupport.summarize(2500.0, null))
            .thenReturn(new ProductDiscountSupport.ProductPriceSummary(2500.0, 2500.0, 0.0));
        when(productDiscountSupport.toDto(null)).thenReturn(null);

        CategorizedProductsDTO result = productService.getProductsSfotRestaurantByCompanyId(companyId);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());

        ProductCategoryDTO bebidas = result.getCategories().stream()
                .filter(c -> "Bebidas".equals(c.getCategoryName()))
                .findFirst()
                .orElse(null);
        assertNotNull(bebidas);
        assertEquals(1, bebidas.getProducts().size());

        ProductDto productDto = bebidas.getProducts().get(0);
        assertEquals(1L, productDto.getId());
        assertEquals("Coca Cola", productDto.getProductName());
        assertEquals(2500.0, productDto.getPrice());
        assertEquals(10L, productDto.getCategoryId());

        verify(categoryRepository, times(1)).findByCompanyId(companyId);
        verify(productRepository, times(1)).findByCompanyIdOrderByNameAsc(companyId);
        verify(companyRepository, times(1)).existsByExternalCompanyId(companyId);
        verifyNoInteractions(callServiceHttp);
    }

    @Test
    void getProductsSfotRestaurantByCompanyId_ShouldReturnEmptyCategories_WhenNoProducts() {
        Long companyId = 1L;
        when(categoryRepository.findByCompanyId(companyId)).thenReturn(Arrays.asList(bebidasCategory));
        when(productRepository.findByCompanyIdOrderByNameAsc(companyId)).thenReturn(Arrays.asList());
        when(companyRepository.existsByExternalCompanyId(companyId)).thenReturn(true); // Mock para la verificación de compañía
        when(productDiscountSupport.findActiveDiscountsByProductIds(eq(companyId), anyList())).thenReturn(java.util.Collections.emptyMap());

        CategorizedProductsDTO result = productService.getProductsSfotRestaurantByCompanyId(companyId);

        assertNotNull(result);
        assertTrue(result.getCategories().isEmpty());

        verify(categoryRepository, times(1)).findByCompanyId(companyId);
        verify(productRepository, times(1)).findByCompanyIdOrderByNameAsc(companyId);
        verify(companyRepository, times(1)).existsByExternalCompanyId(companyId);
        verifyNoInteractions(callServiceHttp);
    }

        @Test
        void getProductsSfotRestaurantByCompanyId_ShouldExposeDiscountedValues() {
        Long companyId = 1L;
        ProductDiscount activeDiscount = ProductDiscount.builder()
            .productDiscountId(99L)
            .productId(1L)
            .companyId(companyId)
            .discountAmount(500.0)
            .status("ACTIVE")
            .build();

        when(categoryRepository.findByCompanyId(companyId)).thenReturn(Arrays.asList(bebidasCategory));
        when(productRepository.findByCompanyIdOrderByNameAsc(companyId)).thenReturn(Arrays.asList(cocaColaProduct));
        when(companyRepository.existsByExternalCompanyId(companyId)).thenReturn(true);
        when(productDiscountSupport.findActiveDiscountsByProductIds(eq(companyId), anyList()))
            .thenReturn(java.util.Map.of(1L, activeDiscount));
        when(productDiscountSupport.summarize(2500.0, activeDiscount))
            .thenReturn(new ProductDiscountSupport.ProductPriceSummary(2500.0, 2000.0, 500.0));
        when(productDiscountSupport.toDto(activeDiscount))
            .thenReturn(com.restaurante.bot.dto.ProductDiscountDto.builder()
                .id(99L)
                .productId(1L)
                .discountAmount(500.0)
                .status("ACTIVE")
                .active(true)
                .build());

        CategorizedProductsDTO result = productService.getProductsSfotRestaurantByCompanyId(companyId);

        ProductDto productDto = result.getCategories().get(0).getProducts().get(0);
        assertEquals(2000.0, productDto.getPrice());
        assertEquals(2500.0, productDto.getOriginalPrice());
        assertEquals(500.0, productDto.getDiscountAmount());
        assertNotNull(productDto.getActiveDiscount());
        assertEquals(99L, productDto.getActiveDiscount().getId());
        }
}