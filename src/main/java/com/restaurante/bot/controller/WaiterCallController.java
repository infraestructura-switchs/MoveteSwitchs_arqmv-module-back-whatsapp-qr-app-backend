package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.WaiterCallInterface;
import com.restaurante.bot.dto.WaiterCallRequest;
import com.restaurante.bot.model.WaiterCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/waitercall")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class WaiterCallController {

    private final WaiterCallInterface waiterCallInterface;

    @GetMapping
    public ResponseEntity<List<WaiterCall>> getWaiterCall() {
        return new ResponseEntity<>(waiterCallInterface.getWaiterCalls(), HttpStatus.OK);
    }

    @PostMapping("/create-waitercall")
    public ResponseEntity<WaiterCall> createWaiterCall(@RequestBody WaiterCallRequest waiterCallRequest) {
        return new ResponseEntity<>(waiterCallInterface.createWaiterCall(waiterCallRequest), HttpStatus.OK);
    }

    @PostMapping("/update-waitercall")
    public ResponseEntity<WaiterCall> updateStatusWaiterCall(@RequestBody WaiterCallRequest waiterCallRequest) {
        return new ResponseEntity<>(waiterCallInterface.updateStatusWaiterCall(waiterCallRequest), HttpStatus.OK);
    }
/*
    @PostMapping
    public WaiterCall guardarWaiterCall(@RequestBody WaiterCall waiterCall ) {
        return waiterCallService.guardarWaiterCall(waiterCall);
    }

 */

}
