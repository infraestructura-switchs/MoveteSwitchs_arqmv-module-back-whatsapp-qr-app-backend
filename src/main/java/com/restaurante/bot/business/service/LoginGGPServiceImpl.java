package com.restaurante.bot.business.service;


import com.restaurante.bot.business.interfaces.LoginGGPService;
import com.restaurante.bot.dto.AbilityDto;
import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;
import com.restaurante.bot.dto.UserDto;
import com.restaurante.bot.exception.CustomErrorException;
import com.restaurante.bot.model.User;
import com.restaurante.bot.repository.UserRepository;
import com.restaurante.bot.security.JwtUtilUser;
import com.restaurante.bot.util.JwtUtil;
import com.restaurante.bot.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginGGPServiceImpl implements LoginGGPService {

    private final UserRepository iRepository;

    private final JwtUtil jwtUtilUser;

    private final Utils utils;

    private final long EXPIRATION_TIME_LONG = 100_000_000;// 27 Horas

    @Override
    public LoginOut login(LoginIn loginIn) {
        LoginOut loginOut = new LoginOut();
        Boolean isCorrect = false;
        String token = "";

        Optional<User> objectOptional = iRepository.findByLogin(loginIn.getUsername());
        UserDto objectDtoVo = null;
        if (objectOptional.isPresent()) {
            objectDtoVo = mapUserDto(objectOptional);

            isCorrect = utils.doPasswordsMatch(loginIn.getPassword(), objectDtoVo.getPassword());

            List<AbilityDto> ability = new ArrayList<AbilityDto>();
            ability.add(new AbilityDto("manage", "all"));

            objectDtoVo.setAbility(ability);

            if (isCorrect) {
                objectDtoVo.setTokenDateExpired(new Date(System.currentTimeMillis() + EXPIRATION_TIME_LONG));
                token = jwtUtilUser.generateToken(objectOptional.get().getCompany().getExternalCompanyId(), objectOptional.get().getUserId());

                objectDtoVo.setToken(token);

                objectDtoVo.setPassword("******");

                loginOut.setData(objectDtoVo);
                loginOut.setStatusCode(HttpStatus.OK.value());
                loginOut.setMessage("success");
            } else {
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Error[Credenciales incorrectas]");
            }
        }

        return loginOut;
    }

    private UserDto mapUserDto(Optional<User> objectUser) {
        UserDto objectDtoVo = new UserDto();
        BeanUtils.copyProperties(objectUser.get(), objectDtoVo);
        objectDtoVo.setRolId(objectUser.get().getRol().getRolId());
        return objectDtoVo;
    }

}
