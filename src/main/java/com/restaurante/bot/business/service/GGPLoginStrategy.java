package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.LoginGGPService;
import com.restaurante.bot.business.interfaces.LoginStrategy;
import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;
import com.restaurante.bot.util.LoginMode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.restaurante.bot.business.service.LoginExecutorImpl.addLoginStrategy;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GGPLoginStrategy implements LoginStrategy {

    private final LoginGGPService iLoginGGPService;

    @PostConstruct
    @Override
    public void apply() {
        addLoginStrategy(LoginMode.GGP_LOGIN,this);
    }

    @Override
    public LoginOut login(LoginIn loginIn) {
        return iLoginGGPService.login(loginIn);
    }
}
