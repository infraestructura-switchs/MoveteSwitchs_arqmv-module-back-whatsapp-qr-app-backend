package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.dto.GgpUserGetAllDto;
import com.restaurante.bot.dto.GgpUserSaveAndUpdateDto;
import com.restaurante.bot.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface UserUseCase {
    UserDto save(GgpUserSaveAndUpdateDto dto);
    UserDto update(Long id, GgpUserSaveAndUpdateDto dto);
    UserDto get(Long id);
    boolean delete(Long id);
    Page<GgpUserGetAllDto> getAll(Map<String,String> filters);
    Page<GgpUserGetAllDto> search(Map<String,String> filters);
    List<GgpUserGetAllDto> getAllWithoutPage(Map<String,String> filters);
    // other operations can be added as needed
}