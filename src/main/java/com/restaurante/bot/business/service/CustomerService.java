package com.restaurante.bot.business.service;


import com.restaurante.bot.business.interfaces.CustomerInterface;
import com.restaurante.bot.dto.SaveFinishDataDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Customer;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.Transaction;
import com.restaurante.bot.repository.CustomerOrderRepository;
import com.restaurante.bot.repository.CustomerRepository;
import com.restaurante.bot.repository.OrderTransactionRepository;
import com.restaurante.bot.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.restaurante.bot.application.ports.incoming.CustomerUseCase;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j

public class CustomerService implements CustomerInterface, CustomerUseCase {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final OrderTransactionRepository orderTransactionRepository;


    public List<Customer> listarClientes() {
        // Cuidado: esta implementación carga todos los clientes en memoria.
        // Use `listarClientesPaged` para evitar OOM en tablas grandes.
        return customerRepository.findAll(); //Esta lista usa el objeto Customer para obtener todos los clientes
    }

    /**
     * Lista clientes paginados para evitar cargar toda la tabla en memoria.
     */
    public Page<Customer> listarClientesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        return customerRepository.findAll(pageable);
    }

    public Customer guardarClientes (Customer customer){
        return customerRepository.save(customer); //Guarda un cliente en la base de datos
    }

    @Override
    public GenericResponse updateClientQr(SaveFinishDataDTO customer) {

        Transaction transaction = transactionRepository.findByTransactionId(customer.getTransactionId());

        if (transaction == null) {
            throw new GenericException("transaccion no encontrada", HttpStatus.BAD_REQUEST);
        }

        transaction.setRatingId(customer.getRatingId());
        transactionRepository.save(transaction);

        Customer customer1 = customerRepository.findByPhone(customer.getPhoneNumber());

        if (customer1 == null) {
            throw new GenericException("cliente no encontrada", HttpStatus.BAD_REQUEST);

        }

        customer1.setEmail(customer.getCustomerEmail());
        customer1.setNumerIdentification(customer.getIdentificationNumber());
        customer1.setTypeIdentificationId(customer.getIdentificationTypeId());
        customer1.setName(customer.getCustomerName());
        customerRepository.save(customer1);

        return new GenericResponse("Actualizacion realizada con exito", 200L);
    }
}
