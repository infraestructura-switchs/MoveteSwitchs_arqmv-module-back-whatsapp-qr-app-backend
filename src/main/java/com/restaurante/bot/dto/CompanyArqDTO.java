package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyArqDTO {

    private Integer companyId;
    private Double descuentoGeneral;
    private List<ArticuloDTO> articulos;
}
