package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.CustomerOrder;

import java.util.List;

public interface CustomerOrderRepositoryPort {
    List<CustomerOrder> findAll();

    CustomerOrder save(CustomerOrder customerOrder);
}