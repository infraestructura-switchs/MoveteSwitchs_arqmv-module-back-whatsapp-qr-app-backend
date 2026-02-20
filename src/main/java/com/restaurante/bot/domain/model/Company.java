package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company {
    private Long id;
    private String name;
    private String logo;
    private String numberWhatsapp;
    private String longitude;
    private String latitude;
    private Double baseValue;
    private Double additionalValue;
    private String status;
    private Long externalCompanyId;
    private Long cityId;
    private String apiKey;
    private String rpIntegrationId;
    private String numberId;
    private String tokenMeta;
    private String numberBotMesa;
    private String numberBotDelivery;
    private String statusIntegrationRp;
    private String tokenMetaDelivery;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
