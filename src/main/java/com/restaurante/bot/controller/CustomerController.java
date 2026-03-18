package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.CustomerUseCase;
import com.restaurante.bot.dto.SaveFinishDataDTO;
import com.restaurante.bot.model.Customer;
import com.restaurante.bot.model.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/customer")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class CustomerController {

    private final CustomerUseCase customerUseCase;

    @GetMapping
    public List<Customer> listarClientes() {
        return customerUseCase.listarClientes(); // Llama al servicio para obtener la lista de clientes
    }

    @PostMapping
    public Customer guardarCliente(@RequestBody Customer customer) {
        return customerUseCase.guardarClientes(customer); // Llama al servicio para guardar un cliente
    }


    @PostMapping("/update-client-qr")
    public ResponseEntity<GenericResponse> updateClientQr(@RequestBody SaveFinishDataDTO customer) {
        log.info("update-client-qr -> {}", customer);
        return new ResponseEntity<>(customerUseCase.updateClientQr(customer), HttpStatus.OK);
    }

}
