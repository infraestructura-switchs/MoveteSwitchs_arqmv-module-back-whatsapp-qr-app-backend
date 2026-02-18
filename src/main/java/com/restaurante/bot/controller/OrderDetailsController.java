package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.OrderInterface;
import com.restaurante.bot.dto.*;
import com.restaurante.bot.model.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/order")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class OrderDetailsController {

    private final OrderInterface orderInterface;

    @PostMapping
    public ResponseEntity<GenericResponse> saveOrder(@RequestBody OrderDetailsDTO order) {
        log.info("Se inicio el procedimiento de guardar una orden con el request -> {}", order);
        return new ResponseEntity<>(orderInterface.saveOrder(order), HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<List<OrderResponseAdminDTO>> getOrders() {
        log.info("Se inicia el servicio que obtiene todas las ordenes activas.");
        return new ResponseEntity<>(orderInterface.getOrders(), HttpStatus.OK);
    }

    @PostMapping("/status/send")
    public ResponseEntity<GenericResponse> sendOrderStatus(@RequestBody OrdersIdsDTO orderIds) {
        log.info("Inicicia el servicio que cambia el estado enviado a la orden");
        return new ResponseEntity<>(orderInterface.sendOrderStatus(orderIds), HttpStatus.OK);
    }
/*
    @GetMapping("/enviada/{tableNumber}")
    public ResponseEntity<List<OrderResponseDTO>> getSendOrder(@PathVariable("tableNumber")Long tableNumber) {
        log.info("Se inicia el servicio para traer las ordenes enviadas por medio de la mesa");
        return new ResponseEntity<>(orderInterface.getSendOrder(tableNumber), HttpStatus.OK);
    }

 */

    @GetMapping("/by-phone/{phoneNumber}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrederByPhoneNumber(@PathVariable("phoneNumber")String phoneNumber) {
        log.info("se inicia el servicio para traer las ordenes por medio del numero de telefono del cliente -> {}", phoneNumber);
        return new ResponseEntity<>(orderInterface.getOrederByPhoneNumber(phoneNumber), HttpStatus.OK);
    }

    @GetMapping("/by-table/{tableNumber}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrederByTableNumber(@PathVariable("tableNumber")Integer tableNumber) {
        log.info("se inicia el servicio para traer las ordenes por medio de la mesa del restaurante -> {}", tableNumber);
        return new ResponseEntity<>(orderInterface.getOrederByTableNumber(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/confirmation")
    public ResponseEntity<GenericResponse> confirmationOrder(@RequestParam String phoneNumber,
                                                             @RequestParam Boolean isConfirmed,
                                                             @RequestParam Long tableNumber) {
        log.info("Se inicia el servicio que confirma la orden");
        return new ResponseEntity<>(orderInterface.confirmationOrder(phoneNumber, isConfirmed, tableNumber), HttpStatus.OK);
    }


    @GetMapping("/no-confirmed")
    public ResponseEntity<List<OrderResponseDTO>> noConfirmationOrder(@RequestParam String phoneNumber,
                                                                      @RequestParam Long tableNumber) {
        log.info("Se inicia el servicio que trae las ordenes no confirmadas");
        return new ResponseEntity<>(orderInterface.noConfirmationOrder(tableNumber, phoneNumber), HttpStatus.OK);
    }

    @GetMapping("/get-all/confirm")
    public ResponseEntity<List<OrderResponseDTO>> confirmedOreders(@RequestParam String phoneNumber,
                                                                   @RequestParam Long tableNumber) {
        log.info("Se inicia el servicio que trae las ordenes  confirmadas");
        return new ResponseEntity<>(orderInterface.confirmedOreders(tableNumber, phoneNumber), HttpStatus.OK);

    }

    @GetMapping("/get-all/company")
    public ResponseEntity<List<CompanyArqDTO>> getOrdersArq(@RequestParam Long companyId) {
        log.info("Se inicia el servicio que obtiene todas las ordenes activas.");
        return new ResponseEntity<>(orderInterface.getOrdersArq(companyId), HttpStatus.OK);
    }

    @PutMapping("/confirm/soft-restaurant")
    public ResponseEntity<GenericResponse>confirmOrdersArq(@RequestBody ConfirmOrderArq request){
        log.info("Se inicia el servicio que va a cambiar el estado de la orden que arq confirme que registro en soft-restaurant");
        return new ResponseEntity<>(orderInterface.confirmOrdersArq(request), HttpStatus.OK);
    }

}
