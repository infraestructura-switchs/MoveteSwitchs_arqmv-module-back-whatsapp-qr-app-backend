package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("waitercall")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaiterCallEntity {
    @Id
    @Column("call_id")
    private Integer callId;
    @Column("table_id")
    private Integer tableId;
    @Column("status")
    private Integer status;
    @Column("time")
    private LocalDateTime time;
}
