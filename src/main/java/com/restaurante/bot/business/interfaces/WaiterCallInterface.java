package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.WaiterCallRequest;
import com.restaurante.bot.model.WaiterCall;

import java.util.List;

public interface WaiterCallInterface {

    List<WaiterCall> getWaiterCalls();

    WaiterCall createWaiterCall(WaiterCallRequest waiterCallRequest);

    WaiterCall updateStatusWaiterCall(WaiterCallRequest waiterCallRequest);

}
