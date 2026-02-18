package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveFinishDataDTO {

    private String phoneNumber;
    private Long identificationNumber;
    private Long identificationTypeId;
    private String customerName;
    private String customerEmail;
    private Long ratingId;
    private Long transactionId;
}
