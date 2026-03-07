package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.model.CustomerOrder;
import java.util.List;

public interface CustomerOrderUseCase {
    List<CustomerOrder> listarOrdenesCliente();
    CustomerOrder guardarOrdenCliente(CustomerOrder customerOrder);
}