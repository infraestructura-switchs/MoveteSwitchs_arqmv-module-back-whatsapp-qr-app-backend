package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("parameter")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParameterEntity {
    @Id
    @Column("parameter_id")
    private Long parameterId;
    @Column("name")
    private String name;
    @Column("value_")
    private String value;
    @Column("status")
    private String status;
    @Column("company_id")
    private Long companyId;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
