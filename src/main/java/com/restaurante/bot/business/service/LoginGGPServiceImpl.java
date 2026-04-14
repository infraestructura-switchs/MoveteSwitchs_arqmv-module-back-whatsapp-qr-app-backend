package com.restaurante.bot.business.service;


import com.restaurante.bot.business.interfaces.LoginGGPService;
import com.restaurante.bot.dto.AbilityDto;
import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;
import com.restaurante.bot.dto.UserDto;
import com.restaurante.bot.exception.CustomErrorException;
import com.restaurante.bot.model.User;
import com.restaurante.bot.repository.UserRepository;
import com.restaurante.bot.security.SessionRegistryService;
import com.restaurante.bot.util.AuthenticationMessages;
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
@RequiredArgsConstructor
public class LoginGGPServiceImpl implements LoginGGPService {

    private final UserRepository iRepository;

    private final JwtUtil jwtUtilUser;

    private final SessionRegistryService sessionRegistryService;

    private final Utils utils;

    private final long EXPIRATION_TIME_LONG = 100_000_000;// 27 Horas

    @Override
    public LoginOut login(LoginIn loginIn) {
        LoginOut loginOut = new LoginOut();
        Boolean isCorrect = false;
        String token = "";

        Optional<User> objectOptional = iRepository.findByLogin(loginIn.getUsername());
        if (objectOptional.isEmpty()) {
            throw new CustomErrorException(HttpStatus.UNAUTHORIZED, 
                AuthenticationMessages.INVALID_CREDENTIALS);
        }
        
        UserDto objectDtoVo = null;
        if (objectOptional.isPresent()) {
            objectDtoVo = mapUserDto(objectOptional);

            isCorrect = utils.doPasswordsMatch(loginIn.getPassword(), objectDtoVo.getPassword());

            List<AbilityDto> ability = new ArrayList<AbilityDto>();
            ability.add(new AbilityDto("manage", "all"));

            objectDtoVo.setAbility(ability);

            if (isCorrect) {
                String sessionId = jwtUtilUser.generateSessionId();
                objectDtoVo.setTokenDateExpired(new Date(System.currentTimeMillis() + EXPIRATION_TIME_LONG));
                
                // Safely retrieve company ID with error handling
                Long externalCompanyId = null;
                try {
                    if (objectOptional.get().getCompany() != null) {
                        externalCompanyId = objectOptional.get().getCompany().getExternalCompanyId();
                    }
                } catch (jakarta.persistence.EntityNotFoundException e) {
                    throw new CustomErrorException(HttpStatus.UNAUTHORIZED, 
                        AuthenticationMessages.AUTHENTICATION_FAILED);
                }
                
                if (externalCompanyId == null) {
                    throw new CustomErrorException(HttpStatus.UNAUTHORIZED, 
                        AuthenticationMessages.AUTHENTICATION_FAILED);
                }
                
                token = jwtUtilUser.generateToken(
                        externalCompanyId,
                        objectOptional.get().getUserId(),
                        sessionId);
                sessionRegistryService.registerSession(
                    sessionId,
                    externalCompanyId,
                    objectOptional.get().getUserId());

                objectDtoVo.setToken(token);
                objectDtoVo.setSessionId(sessionId);

                objectDtoVo.setPassword("******");

                loginOut.setData(objectDtoVo);
                loginOut.setStatusCode(HttpStatus.OK.value());
                loginOut.setMessage(AuthenticationMessages.LOGIN_SUCCESS);
            } else {
                throw new CustomErrorException(HttpStatus.UNAUTHORIZED, AuthenticationMessages.INVALID_CREDENTIALS);
            }
        }

        return loginOut;
    }

    private UserDto mapUserDto(Optional<User> objectUser) {
        UserDto objectDtoVo = new UserDto();
        BeanUtils.copyProperties(objectUser.get(), objectDtoVo);
        objectDtoVo.setRolId(objectUser.get().getRol().getRolId());
        objectDtoVo.setAreaId(objectUser.get().getArea().getAreaId());
        objectDtoVo.setCompanyId(objectUser.get().getCompany().getId());
        objectDtoVo.setPositionId(objectUser.get().getPosition().getPositionId());
        return objectDtoVo;
    }

}
