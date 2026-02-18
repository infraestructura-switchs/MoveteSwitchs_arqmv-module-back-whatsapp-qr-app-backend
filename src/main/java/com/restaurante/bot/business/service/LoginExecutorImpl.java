package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.LoginExecutor;
import com.restaurante.bot.business.interfaces.LoginStrategy;
import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;
import com.restaurante.bot.util.LoginMode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginExecutorImpl implements LoginExecutor {

    private static HashMap<LoginMode, LoginStrategy> loginStrategyMap = new HashMap<>();

    public static void addLoginStrategy(LoginMode loginMode, LoginStrategy loginStrategy) {
        loginStrategyMap.put(loginMode, loginStrategy);
    }
    @Override
    public LoginOut processLogin(LoginMode loginMode, LoginIn loginIn) {
        return loginStrategyMap.get(loginMode)
                .login(loginIn);
    }
}