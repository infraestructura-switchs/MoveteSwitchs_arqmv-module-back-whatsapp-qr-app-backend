package com.restaurante.bot.dto;

import com.restaurante.bot.util.LoginMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginIn {
    @NotBlank(message = "The username is required.")
    private String username;

    @NotBlank(message = "The password is required.")
    private String password;

    @NotBlank(message = "The loginMode is required.")
    private LoginMode loginMode;


}
