package com.restaurante.bot.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AreaDto {

    private Long id;
    private String description;
    private String status;
}
