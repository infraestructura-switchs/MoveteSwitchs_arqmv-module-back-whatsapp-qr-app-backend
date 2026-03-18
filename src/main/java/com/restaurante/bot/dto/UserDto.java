package com.restaurante.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restaurante.bot.dto.RolDto;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String name;
    private String login;
    private String password;
    private String email;
    private RolDto rol;
    private Long rolId;
    private Long companyId;
    private Long positionId;
    private Long areaId;
    private List<AbilityDto> ability;

    private Date tokenDateExpired;
    private String token;
    @JsonProperty("session_id")
    private String sessionId;
    private String status;


}