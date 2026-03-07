package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.model.Payment;
import java.util.List;

public interface PaymentUseCase {
    List<Payment> ListarPayment();
    Payment guardarPayment(Payment payment);
}