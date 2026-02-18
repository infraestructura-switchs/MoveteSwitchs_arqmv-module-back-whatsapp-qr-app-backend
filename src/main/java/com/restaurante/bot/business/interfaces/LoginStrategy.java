package com.restaurante.bot.business.interfaces;


import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;

public interface LoginStrategy {
    void apply();
    LoginOut login(LoginIn loginIn);
}
