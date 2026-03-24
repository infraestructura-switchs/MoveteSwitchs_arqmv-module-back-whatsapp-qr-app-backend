package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyRequest {

    private Long companyId;
    private String nameCompany;
    private String logoUrl;
    private String longitude;
    private String latitude;
    private Double baseValue;
    private Double additionalValue;
    private Long externalCompanyId;
    private Long cityId;
    private CityResponseDTO city;
    private String apiKey;
    private String rpIntegrationId;
    private String landingTemplate;
    private String status;
}
