package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long userId;
    private String name;
    private String login;
    private String password;
    private String email;
    private Long rolId;
    private Long positionId;
    private Long companyId;
    private Long areaId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
