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
    @SequenceGenerator(name = "POSITION_SEQ", sequenceName = "SEQ_POSITION", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSITION_SEQ")
    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;
}
