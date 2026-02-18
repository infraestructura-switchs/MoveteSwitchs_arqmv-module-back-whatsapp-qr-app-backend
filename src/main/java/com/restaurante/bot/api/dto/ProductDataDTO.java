package com.restaurante.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDataDTO {
    private String IdProducto;
    private String Descripcion;
    private Double Precio;
    private Double PrecioSinImpuestos;
    private Double Impuesto1;
    private Double Impuesto2;
    private Double Impuesto3;
    private Boolean VisibleMenu;
    private GroupDTO Grupo;
    private List<Object> subGrupos;
    private List<Object> gruposModificadores;
    private String ImagenMenu;
    private List<String> comentarios;
    private String Informacion;
    private Integer minutosPreparacion;

}
