package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_status")
public class TransactionStatus {

    @Id
    @Column(name = "transaction_status_id")
    private Long transactionStatusId;

    @Column(name = "description")
    private String description;

}
