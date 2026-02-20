package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("subscriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionEntity {
    @Id
    private Long id;
    @Column("user_id")
    private Long userId;
    @Column("token")
    private String token;
    @Column("appointment_notification_enabled")
    private boolean appointmentNotificationEnabled;
    @Column("journal_notification_enabled")
    private boolean journalNotificationEnabled;
}
