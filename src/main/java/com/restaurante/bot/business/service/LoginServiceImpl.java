package com.restaurante.bot.business.service;


import com.restaurante.bot.business.interfaces.LoginExecutor;
import com.restaurante.bot.business.interfaces.LoginService;
import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;
import com.restaurante.bot.util.LoginMode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginServiceImpl implements LoginService {

    private final LoginExecutor loginExecutor;

    @Override
    public LoginOut login(LoginIn loginIn) {
        return loginExecutor.processLogin(Objects.isNull(loginIn.getLoginMode())
                ? LoginMode.GGP_LOGIN: loginIn.getLoginMode(),loginIn);
    }

}
