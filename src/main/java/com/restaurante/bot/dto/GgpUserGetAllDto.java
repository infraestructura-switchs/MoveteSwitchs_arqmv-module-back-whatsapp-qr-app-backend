package com.restaurante.bot.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GgpUserGetAllDto {

    private long userId;
    private String name;
    private String login;
    private String password;
    private String email;
    private RolDto rol;
    private long rolId;
    private String rolName;
    private PositionDto position;
    private CompanyResponseDTO company;
    private AreaDto area;
    private List<AbilityDto> ability;

    private Date tokenDateExpired;
    private String token;
    private String status;

    public GgpUserGetAllDto(long userId, String name, String login, String password, String email, long rolId,
            String rolName, String status) {
        this.userId = userId;
        this.name = name;
        this.login = login;
        this.password = password;
        this.email = email;
        this.rolId = rolId;
        this.rolName = rolName;
        this.status = status;
    }

}