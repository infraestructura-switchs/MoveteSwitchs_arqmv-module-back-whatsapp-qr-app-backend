package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProductDelivery {
    private Long orderProductId;
    private Long orderTransactionDeliveryId;
    private Long productId;
    private String name;
    private Long quantity;
    private Double unitPrice;
    private String commentProduct;
}
