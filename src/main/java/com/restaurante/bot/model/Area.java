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
@Table(name = "area")
public class Area {

    @Id
    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;
}
