package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductGetAllDto {

    private Long id;
    private String productName;
    private Double price;
    private String status;
    private Long categoryId;
    private String description;
    private String image;
    private java.util.List<String> comments;
    private String information;
    private Integer preparationTime;

}
