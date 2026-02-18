package com.restaurante.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

    private String IdGrupo;
    private String Descripcion;
    private String ImagenMenu;
}
