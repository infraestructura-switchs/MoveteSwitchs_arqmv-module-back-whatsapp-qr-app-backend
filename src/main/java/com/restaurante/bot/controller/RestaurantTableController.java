package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.RestaurantTableInterface;
import com.restaurante.bot.dto.NumberDTO;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/restauranttable")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class RestaurantTableController {

    private final RestaurantTableInterface restaurantTableInterface;


    @GetMapping("/get")
    public List<RestaurantTable> ListarMesas() {
        return restaurantTableInterface.ListarMesas();
    }

    @PostMapping("/createTable")
    public ResponseEntity<RestaurantTable> addTable (@RequestParam Long tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.addTable(tableNumber), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{tableId}")
    public ResponseEntity<GenericResponse> deleteTable (@PathVariable("tableId")Long tableId) {
        return new ResponseEntity<>(restaurantTableInterface.deleteTable(tableId), HttpStatus.OK);
    }

    @PostMapping("/change/status-ocuped")
    public ResponseEntity<RestaurantTable> changeStatusOcuped(@RequestBody NumberDTO tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusOcuped(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/change/status-free")
    public ResponseEntity<RestaurantTable> changeStatusFree(@RequestParam Long tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusFree(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/change/status-requesting-service")
    public ResponseEntity<RestaurantTable> changeStatusRequestingService(@RequestBody NumberDTO tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusRequestingService(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/change/status-reserved")
    public ResponseEntity<RestaurantTable> changeStatusReserved(@RequestParam Long tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusReserved(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/change/status-pay")
    public ResponseEntity<RestaurantTable> changeStatusPay(@RequestBody NumberDTO tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusPay(tableNumber), HttpStatus.OK);
    }
}
