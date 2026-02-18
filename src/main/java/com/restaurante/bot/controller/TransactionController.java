package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.TransactionInterface;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${app.request.mapping}/transaction")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class TransactionController {

    private final TransactionInterface transactionInterface;


    @PostMapping("/finish/{tableNumber}")
    public ResponseEntity<GenericResponse> finalizeTransaction(@PathVariable("tableNumber") Integer tableNumber) {
        log.info("Se inicia el servicio que finaliza la transaccion de la mesa -> {}", tableNumber);
        return new ResponseEntity<>(transactionInterface.finalizeTransaction(tableNumber), HttpStatus.OK);
    }
}
