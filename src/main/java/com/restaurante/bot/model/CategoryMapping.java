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
@Table(name = "category_mapping")
public class CategoryMapping {

    @Id
    @SequenceGenerator(name = "CATEGORY-MAPPING-SEQ", sequenceName = "category_mapping_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY-MAPPING-SEQ")
    @Column(name = "mapping_id")
    private Long mappingId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
