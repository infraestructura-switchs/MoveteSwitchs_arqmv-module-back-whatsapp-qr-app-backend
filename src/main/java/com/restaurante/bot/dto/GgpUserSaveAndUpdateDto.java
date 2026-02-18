package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GgpUserSaveAndUpdateDto {

    private Long id;
    private String name;
    private String login;
    private String password;
    private String email;
    private Long rol;
    private Long position;
    private Long company;
    private Long area;
    private String status;
}