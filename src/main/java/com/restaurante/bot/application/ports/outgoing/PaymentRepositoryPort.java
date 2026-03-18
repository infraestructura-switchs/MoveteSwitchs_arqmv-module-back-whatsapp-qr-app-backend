package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.Payment;

import java.util.List;

public interface PaymentRepositoryPort {
    List<Payment> findAll();

    Payment save(Payment payment);
}