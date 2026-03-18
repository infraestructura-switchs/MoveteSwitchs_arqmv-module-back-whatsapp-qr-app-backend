package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.OrderProductRepositoryPort;
import com.restaurante.bot.model.OrderProduct;
import com.restaurante.bot.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderProductRepositoryAdapter implements OrderProductRepositoryPort {

    private final OrderProductRepository orderProductRepository;

    @Override
    public List<OrderProduct> findAll() {
        return orderProductRepository.findAll();
    }

    @Override
    public OrderProduct save(OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }
}