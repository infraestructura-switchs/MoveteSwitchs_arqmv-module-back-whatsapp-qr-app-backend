package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("company")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyEntity {

    @Id
    @Column("company_id")
    private Long id;

    @Column("name")
    private String name;

    @Column("logo")
    private String logo;

    @Column("number_whatsapp")
    private String numberWhatsapp;

    @Column("longitude")
    private String longitude;

    @Column("latitude")
    private String latitude;

    @Column("base_value")
    private Double baseValue;

    @Column("additional_value")
    private Double additionalValue;

    @Column("status")
    private String status;

    @Column("external_company_id")
    private Long externalCompanyId;

    @Column("city_id")
    private Long cityId;

    @Column("api_key")
    private String apiKey;

    @Column("rp_integration_id")
    private String rpIntegrationId;

    @Column("number_id")
    private String numberId;

    @Column("token_meta")
    private String tokenMeta;

    @Column("number_bot_mesa")
    private String numberBotMesa;

    @Column("number_bot_delivery")
    private String numberBotDelivery;

    @Column("STATUS_INTEGRATION_RP")
    private String statusIntegrationRp;

    @Column("token_meta_delivery")
    private String tokenMetaDelivery;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
