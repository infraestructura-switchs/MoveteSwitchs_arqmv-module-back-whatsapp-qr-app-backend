package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("category_mapping")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryMappingEntity {
    @Id
    @Column("mapping_id")
    private Long mappingId;
    @Column("group_id")
    private Long groupId;
    @Column("category_id")
    private Long categoryId;
    @Column("company_id")
    private Long companyId;
    @Column("status")
    private String status;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
