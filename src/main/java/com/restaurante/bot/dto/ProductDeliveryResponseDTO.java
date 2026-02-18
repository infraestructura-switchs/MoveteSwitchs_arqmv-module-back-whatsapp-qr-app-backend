package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDeliveryResponseDTO {

    private Long productId;
    private String name;
    private Double price;
    private String categoryId;
}
