package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.model.GenericResponse;

public interface TransactionInterface {

    GenericResponse finalizeTransaction(Integer tableNumber);
}
