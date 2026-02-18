package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.PositionDto;
import com.restaurante.bot.dto.PositionGetAllDto;
import com.restaurante.bot.dto.PositionSaveAndUpdateDto;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

public interface IPositionService {
    PositionDto save(PositionSaveAndUpdateDto positionDto);
    PositionDto update(Long positionId, PositionSaveAndUpdateDto positionDto);
    boolean delete(Long positionId);
    PositionDto get(Long positionId);
    Page<PositionGetAllDto> getAll(Map<String, String> customQuery);
    Page<PositionGetAllDto> getAll(int page, int size, String orders, String sortBy);
    List<PositionGetAllDto> getAllWithOutPage(Map<String, String> customQuery);
    Page<PositionGetAllDto> searchCustom(Map<String, String> customQuery);
}
