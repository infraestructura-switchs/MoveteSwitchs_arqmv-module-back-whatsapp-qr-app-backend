package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryEntity {
    @Id
    @Column("history_id")
    private Integer historyId;
    @Column("transaction_id")
    private Long transactionId;
    @Column("history_date")
    private LocalDateTime date;
}
