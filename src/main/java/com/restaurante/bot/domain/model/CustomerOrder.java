package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerOrder {
    private Long orderId;
    private Integer status;
    private LocalDateTime date;
    private Long companyId;
    private Double total;
    private Long customerId;
}
