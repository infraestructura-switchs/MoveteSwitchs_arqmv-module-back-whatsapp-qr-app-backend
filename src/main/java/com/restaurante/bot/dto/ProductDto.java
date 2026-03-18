package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    private String productName;
    private Double price;
    private Double originalPrice;
    private Double discountAmount;
    private String description;
    private String status;
    private String image;
    private Long categoryId;
    private CategoryResponseDTO category;
    private Long companyId;
    private List<String> comments;
    private String information;
    private Integer preparationTime;
    private ProductDiscountDto activeDiscount;

}
