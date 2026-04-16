package com.restaurante.bot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "discountAmount is required")
    @Positive(message = "discountAmount must be greater than 0")
    private Double discountAmount;

    @NotBlank(message = "status is required")
    private String status;
}