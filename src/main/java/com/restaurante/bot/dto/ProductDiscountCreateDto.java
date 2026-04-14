package com.restaurante.bot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear (CREATE) product discounts.
 * El productDiscountId se genera automáticamente por la base de datos (secuencia Oracle o auto-increment MySQL).
 * El productId es OPCIONAL - si no se envía, se genera automáticamente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscountCreateDto {

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
