package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.OrderProduct;

import java.util.List;

public interface OrderProductRepositoryPort {
    List<OrderProduct> findAll();

    OrderProduct save(OrderProduct orderProduct);
}