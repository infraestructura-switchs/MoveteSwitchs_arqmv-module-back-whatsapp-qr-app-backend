package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.WaiterCall;

import java.util.List;
import java.util.Optional;

public interface WaiterCallRepositoryPort {
    List<WaiterCall> findAll();

    WaiterCall save(WaiterCall waiterCall);

    WaiterCall findWaiterCallByTableId(Integer tableId);
}