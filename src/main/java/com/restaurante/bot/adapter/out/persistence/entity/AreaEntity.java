package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("area")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaEntity {

    @Id
    @Column("area_id")
    private Long areaId;

    @Column("description")
    private String description;

    @Column("status")
    private String status;
}
