package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDeliveryResponseDTO {


    private Long orderTransactionDeliveryId;
    private Long productId;
    private String name;
    private Long quantity;
    private Double price;
}
