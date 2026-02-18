
package com.restaurante.bot.business.interfaces;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.restaurante.bot.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;


public interface UserService {

    UserDto findByEmail(String email);

    public UserDto save(GgpUserSaveAndUpdateDto userDto);

    public UserDto update(long userId, GgpUserSaveAndUpdateDto user);

    boolean delete(long id);

    UserDto get(long id);

    Page<GgpUserGetAllDto> getAll(Map<String, String> customQuery);


    Page<GgpUserGetAllDto> getAll(int page , int size , String orders ,String sortBy);

    List<GgpUserGetAllDto> getAllWithOutPage(Map<String, String> customQuery);

    Page<GgpUserGetAllDto> searchCustom(Map<String, String> customQuery);

    ForgotPasswordUserDto forgotPassword(GgpForgotPasswordDto ggpForgotPasswordDto) throws UnirestException;
}
