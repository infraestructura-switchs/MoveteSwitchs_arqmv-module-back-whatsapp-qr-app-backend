package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaveAndUpdateDto {

    @NotBlank
    private String productName;

    @NotNull
    private Double price;

    private String description;

    private String status;

    private String image;

    private Long categoryId;

    private String information;

    private Integer preparationTime;
}
