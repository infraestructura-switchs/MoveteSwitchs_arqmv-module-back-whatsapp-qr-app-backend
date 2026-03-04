package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rol")
public class Rol {

    @Id
    @Column(name = "rol_id")
    private Long rolId;

    @Column(name = "name_")
    private String name;

    @Column(name = "status")
    private String status;
}
