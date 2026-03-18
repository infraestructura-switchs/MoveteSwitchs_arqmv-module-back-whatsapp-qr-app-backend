package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.WaiterCallRepositoryPort;
import com.restaurante.bot.model.WaiterCall;
import com.restaurante.bot.repository.WaiterCallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WaiterCallRepositoryAdapter implements WaiterCallRepositoryPort {

    private final WaiterCallRepository waiterCallRepository;

    @Override
    public List<WaiterCall> findAll() {
        return waiterCallRepository.findAll();
    }

    @Override
    public WaiterCall save(WaiterCall waiterCall) {
        return waiterCallRepository.save(waiterCall);
    }

    @Override
    public WaiterCall findWaiterCallByTableId(Integer tableId) {
        return waiterCallRepository.findWaiterCallByTableId(tableId);
    }
}