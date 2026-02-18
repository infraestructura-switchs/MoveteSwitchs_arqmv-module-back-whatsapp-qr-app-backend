package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String productName;
    private Double price;
    private String description;
    private String status;
    private String image;
    private Long categoryId;
    private List<String> comments;
    private String information;
    private Integer preparationTime;

}
