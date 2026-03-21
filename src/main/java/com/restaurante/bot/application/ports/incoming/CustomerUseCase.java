package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.dto.SaveFinishDataDTO;
import com.restaurante.bot.model.Customer;
import com.restaurante.bot.model.GenericResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CustomerUseCase {
    List<Customer> listarClientes();
    Customer guardarClientes(Customer customer);
    GenericResponse updateClientQr(SaveFinishDataDTO customer);

    Customer get(Long id);
    Customer update(Long id, Customer customer);
    boolean delete(Long id);

    Page<Customer> getAll(Map<String, String> customQuery);
    Page<Customer> getAll(int page, int size, String orders, String sortBy);
    List<Customer> getAllWithOutPage(Map<String, String> customQuery);
    Page<Customer> searchCustom(Map<String, String> customQuery);
}