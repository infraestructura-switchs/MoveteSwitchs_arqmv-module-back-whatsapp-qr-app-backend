package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subscriptions")
@Data
public class Subscription {
    @Id
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "appointment_notification_enabled")
    private boolean appointmentNotificationEnabled = false;

    @Column(name = "journal_notification_enabled")
    private boolean journalNotificationEnabled = false;
}