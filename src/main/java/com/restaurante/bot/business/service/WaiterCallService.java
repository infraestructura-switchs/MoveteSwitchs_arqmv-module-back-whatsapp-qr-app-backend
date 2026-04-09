package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.WaiterCallUseCase;
import com.restaurante.bot.application.ports.outgoing.RestaurantTableLookupPort;
import com.restaurante.bot.application.ports.outgoing.WaiterCallRepositoryPort;
import com.restaurante.bot.business.interfaces.WaiterCallInterface;
import com.restaurante.bot.dto.WaiterCallRequest;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.model.RestaurantTable;
import com.restaurante.bot.model.WaiterCall;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaiterCallService implements WaiterCallInterface, WaiterCallUseCase {

    private final WaiterCallRepositoryPort waiterCallRepository;

    private final RestaurantTableLookupPort restaurantTableRepository;


    @Override
    public List<WaiterCall> getWaiterCalls() {
        return waiterCallRepository.findAll();
    }

    @Override
    public WaiterCall createWaiterCall(WaiterCallRequest waiterCallRequest) {
        // Usamos Optional para evitar errores si no se encuentra la mesa
        Optional<RestaurantTable> tableOpt = restaurantTableRepository.findByTableId(waiterCallRequest.getTableId());

        if (tableOpt.isEmpty()) {
            throw new DomainException(DomainErrorCode.NOT_FOUND, "No se encontró mesa");
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
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "No se encontro mesa");
        }

        waiterCall.setStatus(waiterCallRequest.getStatus());
        return waiterCallRepository.save(waiterCall);
    }
}
