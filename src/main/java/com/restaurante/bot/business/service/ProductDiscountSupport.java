package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.ProductDiscountDto;
import com.restaurante.bot.model.ProductDiscount;
import com.restaurante.bot.repository.ProductDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProductDiscountSupport {

    private final ProductDiscountRepository productDiscountRepository;

    public Map<Long, ProductDiscount> findActiveDiscountsByProductIds(Long companyId, Collection<Long> productIds) {
        if (companyId == null || productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ProductDiscount> discounts = productDiscountRepository.findActiveDiscounts(
                companyId,
                productIds,
                LocalDateTime.now());

        Map<Long, ProductDiscount> mappedDiscounts = new LinkedHashMap<>();
        for (ProductDiscount discount : discounts) {
            mappedDiscounts.putIfAbsent(discount.getProductId(), discount);
        }
        return mappedDiscounts;
    }

    public ProductDiscount findActiveDiscount(Long companyId, Long productId) {
        if (companyId == null || productId == null) {
            return null;
        }

        return productDiscountRepository.findActiveDiscountsForProduct(companyId, productId, LocalDateTime.now())
                .stream()
                .findFirst()
                .orElse(null);
    }

    public ProductPriceSummary summarize(Double originalPrice, ProductDiscount discount) {
        Double safeOriginalPrice = originalPrice == null ? null : round(originalPrice);
        if (safeOriginalPrice == null) {
            return new ProductPriceSummary(null, null, 0.0);
        }

        if (discount == null || discount.getDiscountAmount() == null || discount.getDiscountAmount() <= 0) {
            return new ProductPriceSummary(safeOriginalPrice, safeOriginalPrice, 0.0);
        }

        double boundedDiscount = Math.min(discount.getDiscountAmount(), safeOriginalPrice);
        double finalPrice = Math.max(0.0, safeOriginalPrice - boundedDiscount);
        return new ProductPriceSummary(round(safeOriginalPrice), round(finalPrice), round(boundedDiscount));
    }

    public ProductDiscountDto toDto(ProductDiscount discount) {
        if (discount == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean active = discount.getStatus() != null
            && com.restaurante.bot.util.Constants.ACTIVE_STATUS.equalsIgnoreCase(discount.getStatus())
                && !discount.getStartAt().isAfter(now)
                && !discount.getEndAt().isBefore(now);

        return ProductDiscountDto.builder()
                .id(discount.getProductDiscountId())
                .productId(discount.getProductId())
                .companyId(discount.getCompanyId())
                .description(discount.getDescription())
                .discountAmount(discount.getDiscountAmount())
                .startAt(discount.getStartAt())
                .endAt(discount.getEndAt())
                .status(discount.getStatus())
                .active(active)
                .createdAt(discount.getCreatedAt())
                .updatedAt(discount.getUpdatedAt())
                .build();
    }

    private Double round(Double value) {
        return Math.round(Objects.requireNonNull(value) * 100.0d) / 100.0d;
    }

    public record ProductPriceSummary(Double originalPrice, Double finalPrice, Double discountAmount) {
    }
}