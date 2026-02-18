package com.restaurante.bot.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordUserDto {

    private String password;
    private String email;


}