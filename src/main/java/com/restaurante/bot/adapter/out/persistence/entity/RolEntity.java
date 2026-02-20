package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("rol")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolEntity {
    @Id
    @Column("rol_id")
    private Long rolId;
    @Column("name_")
    private String name;
    @Column("status")
    private String status;
}
