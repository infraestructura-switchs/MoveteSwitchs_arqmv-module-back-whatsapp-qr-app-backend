package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.outgoing.CustomerOrderRepositoryPort;
import com.restaurante.bot.model.CustomerOrder;
import org.springframework.stereotype.Service;
import com.restaurante.bot.application.ports.incoming.CustomerOrderUseCase;

import java.util.List;

@Service

public class CustomerOrderService implements CustomerOrderUseCase {

    private final CustomerOrderRepositoryPort customerOrderRepository;

    public CustomerOrderService(CustomerOrderRepositoryPort customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    public  List<CustomerOrder> listarOrdenesCliente() {
        return customerOrderRepository.findAll();
    }

    public CustomerOrder guardarOrdenCliente(CustomerOrder customerOrder) {
        return customerOrderRepository.save(customerOrder);
    }
}
