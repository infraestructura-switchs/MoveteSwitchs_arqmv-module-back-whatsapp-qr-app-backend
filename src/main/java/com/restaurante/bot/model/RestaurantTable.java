package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_table")

public class RestaurantTable {

    @Id
    @SequenceGenerator(name = "RESTAURANT-TABLE-SEQ", sequenceName = "RESTAURANT_TABLE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESTAURANT-TABLE-SEQ")
    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "table_number", nullable = false)
    private Long tableNumber;

    @Column(name = "status")
    private Long status;

    @Column(name = "company_id")
    private Long companyId;
}
