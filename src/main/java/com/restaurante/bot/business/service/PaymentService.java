package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.outgoing.PaymentRepositoryPort;
import com.restaurante.bot.model.Payment;
import org.springframework.stereotype.Service;
import com.restaurante.bot.application.ports.incoming.PaymentUseCase;
import java.util.List;

@Service
public class PaymentService implements PaymentUseCase {
    private final PaymentRepositoryPort paymentRepository;

    public PaymentService(PaymentRepositoryPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> ListarPayment() {
        return paymentRepository.findAll();
    }

    public Payment guardarPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
