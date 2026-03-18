package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscountDto {

    private Long id;
    private Long productId;
    private Long companyId;
    private String description;
    private Double discountAmount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}