package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.ProductDiscountDto;
import com.restaurante.bot.dto.ProductDiscountCreateDto;
import com.restaurante.bot.dto.ProductDiscountSaveAndUpdateDto;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.model.ProductDiscount;
import com.restaurante.bot.repository.ProductDiscountRepository;
import com.restaurante.bot.repository.ProductRepository;
import com.restaurante.bot.util.StatusConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
                .build();

        ProductDiscount saved = ProductDiscount.builder()
                .productDiscountId(1L)
                .productId(8L)
                .companyId(273L)
                .description("Promo almuerzo")
                .discountAmount(5000.0)
                .startAt(java.time.LocalDateTime.now())
                .endAt(java.time.LocalDateTime.now().plusYears(10))
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

        verify(productDiscountRepository).save(argThat(entity ->
                entity.getStartAt() != null
                        && entity.getEndAt() != null
                        && !entity.getEndAt().isBefore(entity.getStartAt())
                        && "ACTIVE".equals(entity.getStatus())));

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
                .description("Promo")
                .discountAmount(5000.0)
                .build();

        when(productRepository.findById(8L)).thenReturn(Optional.of(product));

        DomainException exception = assertThrows(DomainException.class, () -> productDiscountCrudUseCase.save(request));

        assertEquals(DomainErrorCode.INVALID_REQUEST, exception.getCode());
        assertEquals("El descuento no puede ser mayor al precio base del producto", exception.getMessage());
    }

        @Test
        void update_ShouldBuildDatesInBackend_WhenStatusIsInactive() {
                Product product = new Product();
                product.setProductId(8L);
                product.setCompanyId(273L);
                product.setPrice(25000.0);

                ProductDiscount existing = ProductDiscount.builder()
                                .productDiscountId(1L)
                                .productId(8L)
                                .companyId(273L)
                                .description("Promo inicial")
                                .discountAmount(4000.0)
                                .startAt(java.time.LocalDateTime.now().minusDays(2))
                                .endAt(java.time.LocalDateTime.now().plusDays(2))
                                .status("ACTIVE")
                                .build();

                ProductDiscountSaveAndUpdateDto request = ProductDiscountSaveAndUpdateDto.builder()
                                .productId(8L)
                                .companyId(273L)
                                .description("Promo actualizada")
                                .discountAmount(3000.0)
                                .status("INACTIVE")
                                .build();

                when(productDiscountRepository.findByProductDiscountIdAndCompanyIdAndNotDeleted(1L, 273L)).thenReturn(Optional.of(existing));
                when(productRepository.findById(8L)).thenReturn(Optional.of(product));
                when(productDiscountRepository.save(any(ProductDiscount.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(productDiscountSupport.toDto(any(ProductDiscount.class))).thenAnswer(invocation -> {
                        ProductDiscount discount = invocation.getArgument(0);
                        return ProductDiscountDto.builder()
                                        .id(discount.getProductDiscountId())
                                        .productId(discount.getProductId())
                                        .companyId(discount.getCompanyId())
                                        .description(discount.getDescription())
                                        .discountAmount(discount.getDiscountAmount())
                                        .startAt(discount.getStartAt())
                                        .endAt(discount.getEndAt())
                                        .status(discount.getStatus())
                                        .build();
                });

                ProductDiscountDto result = productDiscountCrudUseCase.update(1L, request);

                assertEquals("INACTIVE", result.getStatus());
                assertNotNull(result.getStartAt());
                assertNotNull(result.getEndAt());
                assertEquals(result.getStartAt(), result.getEndAt());
        }

    @Test
    void delete_ShouldMarkDiscountAsDeleted_WhenDiscountExists() {
        ProductDiscount existing = ProductDiscount.builder()
                .productDiscountId(1L)
                .companyId(273L)
                .status(StatusConstants.ACTIVE_STATUS)
                .build();

        when(productDiscountRepository.findByProductDiscountIdAndCompanyIdAndNotDeleted(1L, 273L))
                .thenReturn(Optional.of(existing));
        when(productDiscountRepository.save(any(ProductDiscount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean deleted = productDiscountCrudUseCase.delete(1L, 273L);

        assertEquals(true, deleted);
        verify(productDiscountRepository).save(argThat(discount ->
                StatusConstants.DELETED_STATUS.equals(discount.getStatus())));
    }
}