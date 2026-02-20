package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {
    private Long id;
    private Long userId;
    private String token;
    private boolean appointmentNotificationEnabled;
    private boolean journalNotificationEnabled;
}
