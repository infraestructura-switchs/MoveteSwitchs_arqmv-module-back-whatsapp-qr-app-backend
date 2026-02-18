package com.restaurante.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String idProducto;
    private ProductDataDTO data;
    private Long idCompany;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
