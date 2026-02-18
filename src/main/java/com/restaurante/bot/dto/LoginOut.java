
package com.restaurante.bot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginOut extends GenericResponseOut {

    private UserDto data;

    public LoginOut() {
    }

    public LoginOut(UserDto data) {
        this.data = data;
    }

    public UserDto getData() {
        return data;
    }

    public void setData(UserDto data) {
        this.data = data;
    }

}
