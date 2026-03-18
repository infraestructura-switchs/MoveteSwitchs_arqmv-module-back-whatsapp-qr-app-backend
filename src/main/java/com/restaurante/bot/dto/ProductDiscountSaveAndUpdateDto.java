package com.restaurante.bot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscountSaveAndUpdateDto {

    @NotNull(message = "productId is required")
    @Positive(message = "productId must be greater than 0")
    private Long productId;

    @NotNull(message = "companyId is required")
    @Positive(message = "companyId must be greater than 0")
    private Long companyId;

    private String description;

    @NotNull(message = "discountAmount is required")
    @Positive(message = "discountAmount must be greater than 0")
    private Double discountAmount;

    @NotNull(message = "startAt is required")
    private LocalDateTime startAt;

    @NotNull(message = "endAt is required")
    private LocalDateTime endAt;

    private String status;
}