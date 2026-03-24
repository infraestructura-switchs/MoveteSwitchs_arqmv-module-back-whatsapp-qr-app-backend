package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponseDTO {

    private Long id;
    private String companyName;
    private String logo;
    private String latitude;
    private String longitude;
    private Double baseValue;
    private Double additionalValue;
    private String status;
    private Long externalId;
    private CityResponseDTO city;
    private String apiKey;
    private String rappyId;
    private String landingTemplate;
}
