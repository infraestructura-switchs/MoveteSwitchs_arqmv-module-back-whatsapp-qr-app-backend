package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.RestaurantTableLookupPort;
import com.restaurante.bot.model.RestaurantTable;
import com.restaurante.bot.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantTableLookupAdapter implements RestaurantTableLookupPort {

    private final RestaurantTableRepository restaurantTableRepository;

    @Override
    public Optional<RestaurantTable> findByTableId(Integer tableId) {
        return restaurantTableRepository.findByTableId(tableId);
    }
}