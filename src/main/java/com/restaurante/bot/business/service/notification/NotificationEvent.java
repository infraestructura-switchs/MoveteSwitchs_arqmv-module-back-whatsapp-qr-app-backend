package com.restaurante.bot.business.service.notification;

public class NotificationEvent {
    private final String token;
    private final String title;
    private final String body;

    public NotificationEvent(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
    }

    public String getToken() {
        return token;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
