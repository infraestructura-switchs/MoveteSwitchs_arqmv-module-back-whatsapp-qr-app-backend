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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "table_number", nullable = false)
    private Long tableNumber;

    @Column(name = "status")
    private Long status;

    @Column(name = "company_id")
    private Long companyId;
}
