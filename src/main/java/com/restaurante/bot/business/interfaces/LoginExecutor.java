package com.restaurante.bot.business.interfaces;


import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;
import com.restaurante.bot.util.LoginMode;

public interface LoginExecutor {

    LoginOut processLogin(LoginMode loginMode, LoginIn loginIn) ;
}