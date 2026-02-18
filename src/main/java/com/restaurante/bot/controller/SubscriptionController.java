package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.NotificationInterface;
import com.restaurante.bot.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${app.request.mapping}/subscription")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class SubscriptionController{

    private final NotificationInterface notificationInterface;

    @PostMapping("/firebase")
    public ResponseEntity<Subscription> createSubscription(@RequestParam Long userId, @RequestParam String token) {
        return ResponseEntity.ok(notificationInterface.createOrUpdateSubscription(userId, token));
    }

    @PutMapping("/appointment")
    public ResponseEntity<Subscription> toggleAppointment(@RequestParam Long userId, @RequestParam boolean enabled) {
        return ResponseEntity.ok(notificationInterface.toggleAppointmentNotification(userId, enabled));
    }

    @PutMapping("/journal")
    public ResponseEntity<Subscription> toggleJournal(@RequestParam Long userId, @RequestParam boolean enabled) {
        return ResponseEntity.ok(notificationInterface.toggleJournalNotification(userId, enabled));
    }

    @GetMapping
    public ResponseEntity<Subscription> getSubscription(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationInterface.getSubscription(userId));
    }
}
