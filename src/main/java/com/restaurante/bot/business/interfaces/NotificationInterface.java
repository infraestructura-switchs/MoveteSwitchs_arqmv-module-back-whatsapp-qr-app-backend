package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.model.Subscription;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationInterface {

    Subscription createOrUpdateSubscription(Long userId, String token);

    Subscription toggleAppointmentNotification(Long userId, boolean enabled);

    Subscription toggleJournalNotification(Long userId, boolean enabled);

    Subscription getSubscription(Long userId);

    void sendNotificationToClient(String token, String title, String body);

    List<Subscription> getAppointmentReminders(LocalDateTime start, LocalDateTime end);

    List<Subscription> getJournalReminders(LocalDateTime start, LocalDateTime end);
}
