package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("restaurant_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantTableEntity {

    @Id
    @Column("table_id")
    private Integer tableId;

    @Column("table_number")
    private Long tableNumber;

    @Column("status")
    private Long status;

    @Column("company_id")
    private Long companyId;
}
