package com.restaurante.bot.business.service;

import com.restaurante.bot.model.CustomerOrder;
import com.restaurante.bot.repository.CustomerOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;

    public CustomerOrderService(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    public  List<CustomerOrder> listarOrdenesCliente() {
        return customerOrderRepository.findAll();
    }

    public CustomerOrder guardarOrdenCliente(CustomerOrder customerOrder) {
        return customerOrderRepository.save(customerOrder);
    }
}
