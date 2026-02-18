package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long productId;
    private String arqProductId;
    private String name;
    private Double price;
    private String categoryId;
    private String category;
    private String image;
}
