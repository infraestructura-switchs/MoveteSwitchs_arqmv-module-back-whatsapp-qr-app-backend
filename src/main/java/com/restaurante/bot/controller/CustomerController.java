package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.CustomerUseCase;
import com.restaurante.bot.dto.SaveFinishDataDTO;
import com.restaurante.bot.model.Customer;
import com.restaurante.bot.model.GenericResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Tag(name = "Customer", description = "APIs para la gestión de clientes")
@RestController
@RequestMapping("/${app.request.mapping}/customer")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerUseCase customerUseCase;

    @PostMapping("/create")
    public ResponseEntity<Customer> save(@RequestBody @Valid Customer customer) {
        return ResponseEntity.ok(customerUseCase.guardarClientes(customer));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(customerUseCase.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(customerUseCase.get(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> update(@PathVariable("id") Long customerId,
            @RequestBody @Valid Customer customer) {
        return ResponseEntity.ok(customerUseCase.update(customerId, customer));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        boolean result = customerUseCase.delete(id);
        if (result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Customer>> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = com.restaurante.bot.util.SortConstants.ASC) String orders,
            @RequestParam(defaultValue = "customer_id") String sortBy) {
        return ResponseEntity.ok(customerUseCase.getAll(page, size, orders, sortBy));
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<Customer>> getAllWithoutPage(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(customerUseCase.getAllWithOutPage(customQuery));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Customer>> search(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(customerUseCase.searchCustom(customQuery));
    }

    @PostMapping("/update-client-qr")
    public ResponseEntity<GenericResponse> updateClientQr(@RequestBody SaveFinishDataDTO customer) {
        log.info("update-client-qr -> {}", customer);
        return ResponseEntity.ok(customerUseCase.updateClientQr(customer));
    }

}
