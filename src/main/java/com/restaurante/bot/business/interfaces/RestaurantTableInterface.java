package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.NumberDTO;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;

import java.util.List;

public interface RestaurantTableInterface {

    RestaurantTable changeStatusOcuped(NumberDTO tableNumber);

    RestaurantTable changeStatusFree(Long tableNumber);

    List<RestaurantTable> ListarMesas();

    RestaurantTable addTable(Long tableNumber);

    GenericResponse deleteTable(Long tableId);

    RestaurantTable changeStatusRequestingService(NumberDTO tableNumber);

    RestaurantTable changeStatusReserved(Long tableNumber);

    RestaurantTable changeStatusPay(NumberDTO tableNumber);
}
