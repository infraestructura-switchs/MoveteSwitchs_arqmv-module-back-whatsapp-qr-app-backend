package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponseDTO {

    private Long id;
    private String companyName;
    private String logo;
    private String whatsappNumber;
    private String latitude;
    private String longitude;
    private Double baseValue;
    private Double aditionalValue;
    private String status;
    private Long externalId;
    private Long cityId;
    private String cityName;
    private String apiKey;
    private String rappyId;
    private String numberId;
    private String tokenMetaQr;
    private String numberBotDelivery;
    private String numberBotMesa;
    private String statusRappy;
    private String tokenMetaDelivery;
}
