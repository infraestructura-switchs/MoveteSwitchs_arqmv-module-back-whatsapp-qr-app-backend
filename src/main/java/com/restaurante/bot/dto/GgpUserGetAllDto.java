package com.restaurante.bot.dto;

import com.restaurante.bot.model.Area;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.Position;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GgpUserGetAllDto {

    private long id;
    private String name;
    private String login;
    private String password;
    private String email;
    private RolDto rol;
    private long rolId;
    private String rolName;
    private Position position;
    private Company company;
    private Area Area;
    private List<AbilityDto> ability;

    private Date tokenDateExpired;
    private String token;
    private String status;


}