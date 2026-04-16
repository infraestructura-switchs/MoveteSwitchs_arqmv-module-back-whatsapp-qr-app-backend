package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.ProductDiscountCrudUseCase;
import com.restaurante.bot.dto.ProductDiscountCreateDto;
import com.restaurante.bot.dto.ProductDiscountDto;
import com.restaurante.bot.dto.ProductDiscountSaveAndUpdateDto;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.model.ProductDiscount;
import com.restaurante.bot.repository.ProductDiscountRepository;
import com.restaurante.bot.repository.ProductRepository;
import com.restaurante.bot.util.StatusConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductDiscountCrudUseCaseImpl implements ProductDiscountCrudUseCase {

    private final ProductDiscountRepository productDiscountRepository;
    private final ProductRepository productRepository;
    private final ProductDiscountSupport productDiscountSupport;

    @Override
    @Transactional
    public ProductDiscountDto save(ProductDiscountCreateDto productDiscountDto) {
        // productId es requerido y siempre se valida
        Product product = validateProduct(productDiscountDto.getProductId(), productDiscountDto.getCompanyId());
        validateDiscountAmount(productDiscountDto.getDiscountAmount(), product.getPrice());

        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = startAt.plusYears(10);

        ProductDiscount entity = ProductDiscount.builder()
            .productId(productDiscountDto.getProductId())
            .companyId(productDiscountDto.getCompanyId())
            .description(productDiscountDto.getDescription())
            .discountAmount(productDiscountDto.getDiscountAmount())
            .startAt(startAt)
            .endAt(endAt)
            .status(StatusConstants.ACTIVE_STATUS)
            .build();

        return productDiscountSupport.toDto(productDiscountRepository.save(entity));
    }

    @Override
    public ProductDiscountDto get(Long id, Long companyId) {
        ProductDiscount discount = productDiscountRepository.findByProductDiscountIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new DomainException(DomainErrorCode.NOT_FOUND, "Descuento no encontrado"));
        return productDiscountSupport.toDto(discount);
    }

    @Override
    @Transactional
    public ProductDiscountDto update(Long id, ProductDiscountSaveAndUpdateDto productDiscountDto) {
        ProductDiscount existingDiscount = productDiscountRepository.findByProductDiscountIdAndCompanyId(id, productDiscountDto.getCompanyId())
            .orElseThrow(() -> new DomainException(DomainErrorCode.NOT_FOUND, "Descuento no encontrado"));

        Product product = validateProduct(productDiscountDto.getProductId(), productDiscountDto.getCompanyId());
        validateDiscountAmount(productDiscountDto.getDiscountAmount(), product.getPrice());
        String resolvedStatus = resolveStatus(productDiscountDto.getStatus());
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = StatusConstants.INACTIVE_STATUS.equals(resolvedStatus)
            ? startAt
            : startAt.plusYears(10);

        existingDiscount.setProductId(productDiscountDto.getProductId());
        existingDiscount.setCompanyId(productDiscountDto.getCompanyId());
        existingDiscount.setDescription(productDiscountDto.getDescription());
        existingDiscount.setDiscountAmount(productDiscountDto.getDiscountAmount());
        existingDiscount.setStartAt(startAt);
        existingDiscount.setEndAt(endAt);
        existingDiscount.setStatus(resolvedStatus);

        return productDiscountSupport.toDto(productDiscountRepository.save(existingDiscount));
    }

    @Override
    @Transactional
    public boolean delete(Long id, Long companyId) {
        return productDiscountRepository.findByProductDiscountIdAndCompanyId(id, companyId)
                .map(discount -> {
                    discount.setStatus(StatusConstants.INACTIVE_STATUS);
                    productDiscountRepository.save(discount);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Page<ProductDiscountDto> getAll(Map<String, String> filters, Long companyId) {
        int page = filters != null && filters.containsKey("page") ? Integer.parseInt(filters.get("page")) : 0;
        int size = filters != null && filters.containsKey("size") ? Integer.parseInt(filters.get("size")) : 10;
        Long productId = filters != null && filters.containsKey("productId") ? Long.parseLong(filters.get("productId")) : null;
        String status = filters != null ? filters.get("status") : null;

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDiscount> found = productDiscountRepository.findByFilters(companyId, productId, status, pageable);
        List<ProductDiscountDto> content = found.getContent().stream()
                .map(productDiscountSupport::toDto)
                .toList();
        return new PageImpl<>(content, pageable, found.getTotalElements());
    }

    private Product validateProduct(Long productId, Long companyId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new DomainException(DomainErrorCode.NOT_FOUND, "Producto no encontrado"));

        if (!companyId.equals(product.getCompanyId())) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "El producto no pertenece a la compania indicada");
        }

        return product;
    }

    private void validateDiscountAmount(Double discountAmount, Double productPrice) {
        if (productPrice == null || productPrice <= 0) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "El producto debe tener un precio base valido para aplicar descuento");
        }

        if (discountAmount > productPrice) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "El descuento no puede ser mayor al precio base del producto");
        }
    }

    private String resolveStatus(String status) {
        String resolved = status == null ? "" : status.trim().toUpperCase();
        if (!StatusConstants.ACTIVE_STATUS.equals(resolved) && !StatusConstants.INACTIVE_STATUS.equals(resolved)) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "status debe ser ACTIVE o INACTIVE");
        }
        return resolved;
    }
}