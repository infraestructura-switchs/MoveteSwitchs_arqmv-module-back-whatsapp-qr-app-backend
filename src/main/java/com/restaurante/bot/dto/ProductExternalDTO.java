package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductExternalDTO {

    private Long id;
    private String name;
    private Double price;
    private String description;


}
