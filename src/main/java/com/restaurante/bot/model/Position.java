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
@Table(name = "position")
public class Position {

    @Id
    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;
}
