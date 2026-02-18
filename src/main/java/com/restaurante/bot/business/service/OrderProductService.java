package com.restaurante.bot.business.service;

import com.restaurante.bot.model.OrderProduct;
import com.restaurante.bot.repository.OrderProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }
    public List<OrderProduct> ListarOrderProduct() {
        return orderProductRepository.findAll();
    }

    public OrderProduct guardarOrderProduct(OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }
}
