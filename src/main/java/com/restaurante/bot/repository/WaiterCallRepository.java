package com.restaurante.bot.repository;

import com.restaurante.bot.model.WaiterCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaiterCallRepository extends JpaRepository<WaiterCall, Long> {

    WaiterCall findWaiterCallByTableId(Integer tableId);
}
