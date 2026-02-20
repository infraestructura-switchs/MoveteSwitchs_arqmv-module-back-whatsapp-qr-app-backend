package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("type_identification")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeIdentificationEntity {
    @Id
    @Column("type_identification_id")
    private Long id;
    @Column("name")
    private String name;
    @Column("status")
    private String status;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
