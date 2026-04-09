package com.restaurante.bot.business.service;


import com.restaurante.bot.business.interfaces.CustomerInterface;
import com.restaurante.bot.dto.SaveFinishDataDTO;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
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
import java.util.Map;
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
            throw new DomainException(DomainErrorCode.NOT_FOUND, "transaccion no encontrada");
        }

        transaction.setRatingId(customer.getRatingId());
        transactionRepository.save(transaction);

        Customer customer1 = customerRepository.findByPhone(customer.getPhoneNumber());

        if (customer1 == null) {
            throw new DomainException(DomainErrorCode.NOT_FOUND, "cliente no encontrada");

        }

        customer1.setEmail(customer.getCustomerEmail());
        customer1.setNumerIdentification(customer.getIdentificationNumber());
        customer1.setTypeIdentificationId(customer.getIdentificationTypeId());
        customer1.setName(customer.getCustomerName());
        customerRepository.save(customer1);

        return new GenericResponse("Actualizacion realizada con exito", 200L);
    }

    @Override
    public Customer get(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new DomainException(DomainErrorCode.NOT_FOUND, "cliente no encontrada"));
    }

    @Override
    public Customer update(Long id, Customer customer) {
        Customer existing = customerRepository.findById(id)
            .orElseThrow(() -> new DomainException(DomainErrorCode.NOT_FOUND, "cliente no encontrada"));

        if (customer.getName() != null) existing.setName(customer.getName());
        if (customer.getEmail() != null) existing.setEmail(customer.getEmail());
        if (customer.getPhone() != null) existing.setPhone(customer.getPhone());
        if (customer.getAddress() != null) existing.setAddress(customer.getAddress());
        if (customer.getTypeIdentificationId() != null) existing.setTypeIdentificationId(customer.getTypeIdentificationId());
        if (customer.getNumerIdentification() != null) existing.setNumerIdentification(customer.getNumerIdentification());

        return customerRepository.save(existing);
    }

    @Override
    public boolean delete(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Page<Customer> getAll(Map<String, String> customQuery) {
        int page = 0;
        int size = 5;
        try {
            if (customQuery.containsKey("page")) page = Integer.parseInt(customQuery.get("page"));
            if (customQuery.containsKey("size")) size = Integer.parseInt(customQuery.get("size"));
        } catch (NumberFormatException e) {
            // ignore and use defaults
        }
        return listarClientesPaged(page, size);
    }

    @Override
    public Page<Customer> getAll(int page, int size, String orders, String sortBy) {
        return listarClientesPaged(page, size);
    }

    @Override
    public List<Customer> getAllWithOutPage(Map<String, String> customQuery) {
        return listarClientes();
    }

    @Override
    public Page<Customer> searchCustom(Map<String, String> customQuery) {
        // Basic placeholder: returns first page. Implement filtering as needed.
        return listarClientesPaged(0, 5);
    }
}
