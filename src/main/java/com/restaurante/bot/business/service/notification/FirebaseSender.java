package com.restaurante.bot.business.service.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

public interface FirebaseSender {
    void send(Message message) throws FirebaseMessagingException;
}
