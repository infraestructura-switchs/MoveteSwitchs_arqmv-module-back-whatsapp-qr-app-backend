package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerEntity {

    @Id
    @Column("customer_id")
    private Long customerId;

    @Column("name")
    private String name;

    @Column("phone")
    private String phone;

    @Column("email")
    private String email;

    @Column("address")
    private String address;

    @Column("type_identification_id")
    private Long typeIdentificationId;

    @Column("numer_identification")
    private Long numerIdentification;
}
