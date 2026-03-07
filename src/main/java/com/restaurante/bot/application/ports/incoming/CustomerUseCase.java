package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.dto.SaveFinishDataDTO;
import com.restaurante.bot.model.Customer;
import com.restaurante.bot.model.GenericResponse;

import java.util.List;

public interface CustomerUseCase {
    List<Customer> listarClientes();
    Customer guardarClientes(Customer customer);
    GenericResponse updateClientQr(SaveFinishDataDTO customer);
}