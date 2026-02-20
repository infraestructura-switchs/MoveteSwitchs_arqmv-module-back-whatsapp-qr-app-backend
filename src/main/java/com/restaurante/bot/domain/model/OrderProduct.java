package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProduct {
    private Integer orderProductId;
    private Long orderId;
    private String productId;
    private Integer quantity;
    private String commentProduct;
    private Long companyId;
}
