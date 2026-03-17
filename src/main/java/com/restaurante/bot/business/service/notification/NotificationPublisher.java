package com.restaurante.bot.business.service.notification;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationPublisher {
    private final List<NotificationObserver> observers = new CopyOnWriteArrayList<>();

    public void register(NotificationObserver observer) {
        observers.add(observer);
    }

    public void unregister(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void publish(NotificationEvent event) {
        observers.forEach(o -> {
            try {
                o.onNotification(event);
            } catch (Exception e) {
                // log and continue with other observers
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        });
    }

    public void publish(String token, String title, String body) {
        publish(new NotificationEvent(token, title, body));
    }
}
