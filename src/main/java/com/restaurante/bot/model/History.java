package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "history")
public class History {

    @Id
    @Column(name = "history_id")
    private Integer historyId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "history_date")
    private LocalDateTime date;

}
