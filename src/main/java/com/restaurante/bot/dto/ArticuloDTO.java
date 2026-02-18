package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloDTO {

    private String idcuenta;
    private String orderId;
    private String idproducto;
    private String desc;
    private String cantidad;
    private String precio;
    private String comentario;
    private String hora;
    private String descuento;
}
