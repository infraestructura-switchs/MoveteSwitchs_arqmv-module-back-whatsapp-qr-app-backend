
package com.restaurante.bot.business.interfaces;


import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;

public interface LoginGGPService {

    public LoginOut login(LoginIn loginIn);
}
