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
public class OrderDetailDelivery {
    private Long orderTransactionDeliveryId;
    private Long paymentId;
    private Long customerId;
    private String method;
    private Double total;
    private String status;
    private String statusOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
