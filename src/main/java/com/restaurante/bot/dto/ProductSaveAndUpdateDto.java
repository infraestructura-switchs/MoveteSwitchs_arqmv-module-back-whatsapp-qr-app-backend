package com.restaurante.bot.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.restaurante.bot.util.FlexibleStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
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

    @JsonDeserialize(using = FlexibleStringDeserializer.class)
    private String information;

    private Integer preparationTime;
    @NotNull
    private Long companyId;

    private java.util.List<String> comments;
}
