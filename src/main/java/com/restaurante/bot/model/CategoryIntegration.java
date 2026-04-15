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
@Table(name = "category_integration")
public class CategoryIntegration {

    @Id
    @SequenceGenerator(name = "CATEGORY-INTEGRATION-SEQ", sequenceName = "CATEGORY_INTEGRATION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY-INTEGRATION-SEQ")
    @Column(name = "category_integration_id")
    private Long categoryIntegrationId;

    @Column(name = "name", length = 500)
    private String name;

    @Column(name = "external_id")
    private Long externalId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "status", length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
