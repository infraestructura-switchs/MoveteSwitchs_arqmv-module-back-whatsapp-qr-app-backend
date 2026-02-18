package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parameter")
public class Parameter {

    @Id
    @SequenceGenerator(name = "PARAMETER-SEQ", sequenceName = "PARAMETER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARAMETER-SEQ")
    @Column(name = "parameter_id")
    private Long parameterId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value_", nullable = false)
    private String value;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}