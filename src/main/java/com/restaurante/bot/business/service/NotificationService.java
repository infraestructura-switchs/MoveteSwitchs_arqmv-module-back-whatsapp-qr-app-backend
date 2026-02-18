package com.restaurante.bot.business.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.restaurante.bot.business.interfaces.NotificationInterface;
import com.restaurante.bot.model.Subscription;
import com.restaurante.bot.model.User;
import com.restaurante.bot.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationService implements NotificationInterface {

    private final SubscriptionRepository subscriptionRepository;

    // Crear o actualizar suscripción con token
    public Subscription createOrUpdateSubscription(Long userId, String token) {
        Subscription sub = subscriptionRepository.findByUserId(userId);
        if (sub == null) {
            sub = new Subscription();
            sub.setUserId(userId);
        }
        sub.setToken(token);
        return subscriptionRepository.save(sub);
    }

    // Habilitar/deshabilitar notificaciones para appointments
    public Subscription toggleAppointmentNotification(Long userId, boolean enabled) {
        Subscription sub = subscriptionRepository.findByUserId(userId);
        if (sub == null) {
            throw new RuntimeException("No subscription found");
        }
        sub.setAppointmentNotificationEnabled(enabled);
        return subscriptionRepository.save(sub);
    }

    // Similar para journals
    public Subscription toggleJournalNotification(Long userId, boolean enabled) {
        Subscription sub = subscriptionRepository.findByUserId(userId);
        if (sub == null) {
            throw new RuntimeException("No subscription found");
        }
        sub.setJournalNotificationEnabled(enabled);
        return subscriptionRepository.save(sub);
    }

    // Obtener suscripción por usuario
    public Subscription getSubscription(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    // Método para enviar notificación a un token específico
    public void sendNotificationToClient(String token, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            System.out.println("Notificación enviada a: " + token);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error enviando notificación: " + e.getMessage());
        }
    }

    // Lógica para obtener recordatorios (adapta a tu DB Oracle)
    // Ejemplo: Busca appointments o journals pendientes en los próximos 5 min
    public List<Subscription> getAppointmentReminders(LocalDateTime start, LocalDateTime end) {
        // Implementa consulta JPA a tu tabla de appointments/journals
        // Por ej., filtra por fecha y usuarios suscritos
        // Retorna lista de subscriptions que necesitan notificación
        return List.of(); // Placeholder
    }

    // Similar para journals
    public List<Subscription> getJournalReminders(LocalDateTime start, LocalDateTime end) {
        return List.of(); // Placeholder
    }
}
