package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private Long productId;
    private Integer arqProductId;
    private String name;
    private Double price;
    private String description;
    private Long categoryId;
    private Long groupId;
    private Long companyId;
    private String status;
    private String imgProduct;
    private String comments;
    private String information;
    private Integer preparationTime;
    private String softRestaurantId;
}
