package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.CustomerOrderRepositoryPort;
import com.restaurante.bot.model.CustomerOrder;
import com.restaurante.bot.repository.CustomerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerOrderRepositoryAdapter implements CustomerOrderRepositoryPort {

    private final CustomerOrderRepository customerOrderRepository;

    @Override
    public List<CustomerOrder> findAll() {
        return customerOrderRepository.findAll();
    }

    @Override
    public CustomerOrder save(CustomerOrder customerOrder) {
        return customerOrderRepository.save(customerOrder);
    }
}