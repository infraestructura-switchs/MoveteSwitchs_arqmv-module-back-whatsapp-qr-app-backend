package com.restaurante.bot.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.restaurante.bot.util.FlexibleStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaveAndUpdateDto {

    @NotBlank(message = "productName is required")
    private String productName;

    @NotNull(message = "price is required")
    @Positive(message = "price must be greater than 0")
    private Double price;

    private Double originalPrice;

    private String description;

    private String status;

    private String image;

    @NotNull(message = "categoryId is required")
    @Positive(message = "categoryId must be greater than 0")
    private Long categoryId;

    @JsonDeserialize(using = FlexibleStringDeserializer.class)
    private String information;

    @NotNull(message = "preparationTime is required")
    @Positive(message = "preparationTime must be greater than 0")
    private Integer preparationTime;

    @Positive(message = "companyId must be greater than 0")
    private Long companyId;

    private java.util.List<String> comments;
}
