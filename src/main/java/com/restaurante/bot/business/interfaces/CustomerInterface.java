package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.SaveFinishDataDTO;
import com.restaurante.bot.model.GenericResponse;

public interface CustomerInterface {

    GenericResponse updateClientQr( SaveFinishDataDTO customer);
}
