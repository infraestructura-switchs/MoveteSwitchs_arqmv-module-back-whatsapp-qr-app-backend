package com.restaurante.bot.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.model.ProductDiscount;
import com.restaurante.bot.repository.CategoryRepository;
import com.restaurante.bot.repository.CommentRepository;
import com.restaurante.bot.repository.ProductCommentRepository;
import com.restaurante.bot.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCrudUseCaseImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCommentRepository productCommentRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProductDiscountSupport productDiscountSupport;

    @InjectMocks
    private ProductCrudUseCaseImpl productCrudUseCase;

    @Test
    void save_ShouldStoreNullInformation_WhenPayloadContainsEmptyArrayString() {
        ProductSaveAndUpdateDto request = ProductSaveAndUpdateDto.builder()
                .productName("Carne Asadaa")
                .price(34000.0)
                .companyId(273L)
                .information("[]")
                .build();

        Product persistedProduct = new Product();
        persistedProduct.setProductId(649L);
        persistedProduct.setName("Carne Asadaa");
        persistedProduct.setPrice(34000.0);
        persistedProduct.setCompanyId(273L);
        persistedProduct.setInformation(null);

        when(productRepository.save(any(Product.class))).thenReturn(persistedProduct);
        when(productCommentRepository.findByProductId(any())).thenReturn(java.util.Collections.emptyList());
        when(productDiscountSupport.findActiveDiscount(273L, 649L)).thenReturn(null);
        when(productDiscountSupport.summarize(34000.0, null))
            .thenReturn(new ProductDiscountSupport.ProductPriceSummary(34000.0, 34000.0, 0.0));
        when(productDiscountSupport.toDto(null)).thenReturn(null);

        ProductDto response = productCrudUseCase.save(request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertNull(productCaptor.getValue().getInformation());
        assertNull(response.getInformation());
    }

    @Test
    void update_ShouldPreserveInformation_WhenPayloadContainsEmptyArrayString() {
        Product existingProduct = new Product();
        existingProduct.setProductId(649L);
        existingProduct.setName("Carne Asada");
        existingProduct.setPrice(34000.0);
        existingProduct.setCompanyId(273L);
        existingProduct.setInformation("valor anterior");

        ProductSaveAndUpdateDto request = ProductSaveAndUpdateDto.builder()
                .productName("Carne Asadaa")
                .price(34000.0)
                .companyId(273L)
                .information("[]")
                .build();

        Product updatedProduct = new Product();
        updatedProduct.setProductId(649L);
        updatedProduct.setName("Carne Asadaa");
        updatedProduct.setPrice(34000.0);
        updatedProduct.setCompanyId(273L);
        updatedProduct.setInformation("valor anterior");

        when(productRepository.findById(649L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(productCommentRepository.findByProductId(any())).thenReturn(java.util.Collections.emptyList());
        when(productDiscountSupport.findActiveDiscount(273L, 649L)).thenReturn(null);
        when(productDiscountSupport.summarize(34000.0, null))
            .thenReturn(new ProductDiscountSupport.ProductPriceSummary(34000.0, 34000.0, 0.0));
        when(productDiscountSupport.toDto(null)).thenReturn(null);

        ProductDto response = productCrudUseCase.update(649L, request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertEquals("valor anterior", productCaptor.getValue().getInformation());
        assertEquals("valor anterior", response.getInformation());
        assertEquals("Carne Asadaa", productCaptor.getValue().getName());
    }

    @Test
    void get_ShouldExposeDiscountedPrice_WhenActiveDiscountExists() {
        Product existingProduct = new Product();
        existingProduct.setProductId(649L);
        existingProduct.setName("Carne Asada");
        existingProduct.setPrice(34000.0);
        existingProduct.setCompanyId(273L);

        ProductDiscount activeDiscount = ProductDiscount.builder()
                .productDiscountId(10L)
                .productId(649L)
                .companyId(273L)
                .discountAmount(4000.0)
                .status("ACTIVE")
                .build();

        when(productRepository.findById(649L)).thenReturn(Optional.of(existingProduct));
        when(productCommentRepository.findByProductId(any())).thenReturn(java.util.Collections.emptyList());
        when(productDiscountSupport.findActiveDiscount(273L, 649L)).thenReturn(activeDiscount);
        when(productDiscountSupport.summarize(34000.0, activeDiscount))
                .thenReturn(new ProductDiscountSupport.ProductPriceSummary(34000.0, 30000.0, 4000.0));

        ProductDto response = productCrudUseCase.get(649L);

        assertEquals(30000.0, response.getPrice());
        assertEquals(34000.0, response.getOriginalPrice());
        assertEquals(4000.0, response.getDiscountAmount());
    }
}