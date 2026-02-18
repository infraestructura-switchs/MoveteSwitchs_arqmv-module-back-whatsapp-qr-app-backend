package com.restaurante.bot.config;

import com.restaurante.bot.business.service.NotificationService;
import com.restaurante.bot.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component

public class NotificationScheduler {

    @Autowired
    private NotificationService notificationService;

   // @Scheduled(cron = "0 */1 * * * *")
    public void notifyUsers() {
        System.out.println("---NOTIFYING USERS---");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minus(5, ChronoUnit.MINUTES);
        LocalDateTime end = now;

        // Ajusta tiempos como en el artículo (e.g., 65 min para appointments)
        LocalDateTime appointmentStart = now.plus(65, ChronoUnit.MINUTES).minus(5, ChronoUnit.MINUTES); // Ejemplo
        LocalDateTime appointmentEnd = now.plus(65, ChronoUnit.MINUTES);

        List<Subscription> journalReminders = notificationService.getJournalReminders(start, end);
        List<Subscription> appointmentReminders = notificationService.getAppointmentReminders(appointmentStart, appointmentEnd);

        // Envía notificaciones
        journalReminders.forEach(sub -> {
            if (sub.isJournalNotificationEnabled()) {
                notificationService.sendNotificationToClient(sub.getToken(), "Recordatorio Journal", "Tienes un journal pendiente!");
            }
        });

        appointmentReminders.forEach(sub -> {
            if (sub.isAppointmentNotificationEnabled()) {
                notificationService.sendNotificationToClient(sub.getToken(), "Recordatorio Appointment", "Tienes una cita pronto!");
            }
        });
    }
}
