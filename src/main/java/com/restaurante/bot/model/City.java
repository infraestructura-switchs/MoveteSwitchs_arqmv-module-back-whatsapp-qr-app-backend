package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "CITY")
public class City {

    @Id
    @SequenceGenerator(name = "CITY-SEQ", sequenceName = "CITY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CITY-SEQ")
    @Column(name = "CITY_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATUS")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
