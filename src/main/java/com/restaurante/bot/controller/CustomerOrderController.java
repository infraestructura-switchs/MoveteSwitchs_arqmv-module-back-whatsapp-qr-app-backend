package com.restaurante.bot.controller;

import com.restaurante.bot.model.CustomerOrder;
import com.restaurante.bot.application.ports.incoming.CustomerOrderUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/customerorder")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class CustomerOrderController {

    private final CustomerOrderUseCase customerOrderService;

    public CustomerOrderController(CustomerOrderUseCase customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    @GetMapping
    public List<CustomerOrder> listarOrdenesCliente() {
        return customerOrderService.listarOrdenesCliente();
    }

    @PostMapping
    public CustomerOrder guardarOrdenCliente(@RequestBody CustomerOrder customerOrder) {
        return customerOrderService.guardarOrdenCliente(customerOrder);
    }
}
