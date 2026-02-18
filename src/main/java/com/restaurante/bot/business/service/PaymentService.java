package com.restaurante.bot.business.service;

import com.restaurante.bot.model.Payment;
import com.restaurante.bot.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> ListarPayment() {
        return paymentRepository.findAll();
    }

    public Payment guardarPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
