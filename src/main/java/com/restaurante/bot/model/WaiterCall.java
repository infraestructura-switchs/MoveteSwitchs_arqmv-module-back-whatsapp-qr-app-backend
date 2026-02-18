package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "waitercall")
public class WaiterCall {

    @Id
    @SequenceGenerator(name = "WAITERCALL-SEQ", sequenceName = "WAITERCALL_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WAITERCALL-SEQ")
    @Column(name = "call_id")
    private Integer callId;

    @Column(name = "table_id", nullable = false)
    private Integer tableId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "time" , nullable = false)
    private LocalDateTime time;

}
