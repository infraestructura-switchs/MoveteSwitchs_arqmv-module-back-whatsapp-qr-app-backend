package com.restaurante.bot.dto;

import com.restaurante.bot.util.LoginMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateLinkIn {

    @NotBlank(message = "The companyId is required.")
    private Long companyId;
    @NotBlank(message = "The userToken is required.")
    private String userToken;

    private Long userId;

    @NotBlank(message = "The mesa is required.")
    private String mesa;
    private String qr;
    private String Delivery;

}
