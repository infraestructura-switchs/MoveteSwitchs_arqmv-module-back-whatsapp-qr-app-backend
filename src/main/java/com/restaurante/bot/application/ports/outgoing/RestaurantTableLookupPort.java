package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.RestaurantTable;

import java.util.Optional;

public interface RestaurantTableLookupPort {
    Optional<RestaurantTable> findByTableId(Integer tableId);
}