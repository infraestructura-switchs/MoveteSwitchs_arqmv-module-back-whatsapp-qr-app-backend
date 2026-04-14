package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.ProductDiscountDto;
import com.restaurante.bot.dto.ProductDiscountCreateDto;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.model.ProductDiscount;
import com.restaurante.bot.repository.ProductDiscountRepository;
import com.restaurante.bot.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDiscountCrudUseCaseImplTest {

    @Mock
    private ProductDiscountRepository productDiscountRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDiscountSupport productDiscountSupport;

    @InjectMocks
    private ProductDiscountCrudUseCaseImpl productDiscountCrudUseCase;

    @Test
    void save_ShouldCreateDiscount_WhenPayloadIsValid() {
        Product product = new Product();
        product.setProductId(8L);
        product.setCompanyId(273L);
        product.setPrice(25000.0);

        ProductDiscountCreateDto request = ProductDiscountCreateDto.builder()
                .productId(8L)
                .companyId(273L)
                .description("Promo almuerzo")
                .discountAmount(5000.0)
                .startAt(LocalDateTime.of(2026, 3, 18, 10, 0))
                .endAt(LocalDateTime.of(2026, 3, 18, 18, 0))
                .build();

        ProductDiscount saved = ProductDiscount.builder()
                .productDiscountId(1L)
                .productId(8L)
                .companyId(273L)
                .description("Promo almuerzo")
                .discountAmount(5000.0)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .status("ACTIVE")
                .build();

        ProductDiscountDto response = ProductDiscountDto.builder()
                .id(1L)
                .productId(8L)
                .companyId(273L)
                .discountAmount(5000.0)
                .status("ACTIVE")
                .build();

        when(productRepository.findById(8L)).thenReturn(Optional.of(product));
        when(productDiscountRepository.save(any(ProductDiscount.class))).thenReturn(saved);
        when(productDiscountSupport.toDto(saved)).thenReturn(response);

        ProductDiscountDto result = productDiscountCrudUseCase.save(request);

        assertEquals(1L, result.getId());
        assertEquals(5000.0, result.getDiscountAmount());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void save_ShouldRejectDiscountGreaterThanProductPrice() {
        Product product = new Product();
        product.setProductId(8L);
        product.setCompanyId(273L);
        product.setPrice(4000.0);

        ProductDiscountCreateDto request = ProductDiscountCreateDto.builder()
                .productId(8L)
                .companyId(273L)
                .discountAmount(5000.0)
                .startAt(LocalDateTime.of(2026, 3, 18, 10, 0))
                .endAt(LocalDateTime.of(2026, 3, 18, 18, 0))
                .build();

        when(productRepository.findById(8L)).thenReturn(Optional.of(product));

        DomainException exception = assertThrows(DomainException.class, () -> productDiscountCrudUseCase.save(request));

        assertEquals(DomainErrorCode.INVALID_REQUEST, exception.getCode());
        assertEquals("El descuento no puede ser mayor al precio base del producto", exception.getMessage());
    }
}