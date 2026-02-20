package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("user_app")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @Column("user_id")
    private Long userId;

    @Column("name_")
    private String name;

    @Column("login")
    private String login;

    @Column("password")
    private String password;

    @Column("email")
    private String email;

    @Column("rol_id")
    private Long rolId;

    @Column("position_id")
    private Long positionId;

    @Column("company_id")
    private Long companyId;

    @Column("area_id")
    private Long areaId;

    @Column("status")
    private String status;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
