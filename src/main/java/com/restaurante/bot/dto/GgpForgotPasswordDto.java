package com.restaurante.bot.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GgpForgotPasswordDto {

    private String email;
}