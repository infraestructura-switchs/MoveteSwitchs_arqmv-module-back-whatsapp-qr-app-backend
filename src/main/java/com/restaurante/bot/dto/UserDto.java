package com.restaurante.bot.dto;

import com.restaurante.bot.model.Rol;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String name;
    private String login;
    private String password;
    private String email;
    private Rol rol;
    private Long rolId;
    private Long companyId;
    private Long positionId;
    private Long areaId;
    private List<AbilityDto> ability;

    private Date tokenDateExpired;
    private String token;
    private String status;


}