
package com.restaurante.bot.business.interfaces;


import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;

public interface LoginService {

    public LoginOut login(LoginIn loginIn);
}
