package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PositionSaveAndUpdateDto {
    private Long id;
    private String description;
    private String status;
}
