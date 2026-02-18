package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.WaiterCallInterface;
import com.restaurante.bot.dto.WaiterCallRequest;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.RestaurantTable;
import com.restaurante.bot.model.WaiterCall;
import com.restaurante.bot.repository.RestaurantTableRepository;
import com.restaurante.bot.repository.WaiterCallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WaiterCallService implements WaiterCallInterface {

    private  final WaiterCallRepository waiterCallRepository;

    private final RestaurantTableRepository restaurantTableRepository;


    @Override
    public List<WaiterCall> getWaiterCalls() {
        return waiterCallRepository.findAll();
    }

    @Override
    public WaiterCall createWaiterCall(WaiterCallRequest waiterCallRequest) {
        // Usamos Optional para evitar errores si no se encuentra la mesa
        Optional<RestaurantTable> tableOpt = restaurantTableRepository.findByTableId(waiterCallRequest.getTableId());

        if (tableOpt.isEmpty()) {
            throw new GenericException("No se encontró mesa", HttpStatus.NOT_FOUND);
        }

        WaiterCall call = new WaiterCall();
        call.setTableId(waiterCallRequest.getTableId());
        call.setStatus(waiterCallRequest.getStatus() != null ? waiterCallRequest.getStatus() : 1); // por defecto status = 1
        call.setTime(LocalDateTime.now());

        return waiterCallRepository.save(call);
    }

    @Override
    public WaiterCall updateStatusWaiterCall(WaiterCallRequest waiterCallRequest) {
        WaiterCall waiterCall = waiterCallRepository.findWaiterCallByTableId(waiterCallRequest.getTableId());

        if (waiterCall == null) {
            throw new GenericException("No se encontro mesa", HttpStatus.BAD_REQUEST);
        }

        waiterCall.setStatus(waiterCallRequest.getStatus());
        return waiterCallRepository.save(waiterCall);
    }
}
