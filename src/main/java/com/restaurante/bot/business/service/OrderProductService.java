package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.outgoing.OrderProductRepositoryPort;
import com.restaurante.bot.model.OrderProduct;
import org.springframework.stereotype.Service;
import com.restaurante.bot.application.ports.incoming.OrderProductUseCase;
import java.util.List;

@Service

public class OrderProductService implements OrderProductUseCase {
    private final OrderProductRepositoryPort orderProductRepository;

    public OrderProductService(OrderProductRepositoryPort orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }
    public List<OrderProduct> ListarOrderProduct() {
        return orderProductRepository.findAll();
    }

    public OrderProduct guardarOrderProduct(OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }
}
