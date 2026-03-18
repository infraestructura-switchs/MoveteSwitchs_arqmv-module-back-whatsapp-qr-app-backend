package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductGetAllDto {

    private Long id;
    private String productName;
    private Double price;
    private Double originalPrice;
    private Double discountAmount;
    private String status;
    private Long categoryId;
    private CategoryResponseDTO category;
    private String description;
    private String image;
    private java.util.List<String> comments;
    private Long companyId;
    private String information;
    private Integer preparationTime;
    private ProductDiscountDto activeDiscount;

}
