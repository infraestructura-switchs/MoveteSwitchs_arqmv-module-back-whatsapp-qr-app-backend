package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("customer_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerOrderEntity {

    @Id
    @Column("order_id")
    private Long orderId;

    @Column("status")
    private Integer status;

    @Column("customer_order_date")
    private LocalDateTime date;

    @Column("company_id")
    private Long companyId;

    @Column("total")
    private Double total;

    @Column("customer_id")
    private Long customerId;
}
