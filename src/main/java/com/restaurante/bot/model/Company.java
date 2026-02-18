package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "company")
public class Company {
    @Id
    @SequenceGenerator(name = "COMPANY-SEQ", sequenceName = "COMPANY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY-SEQ")
    @Column(name = "company_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "logo")
    private String logo;

    @Column(name = "number_whatsapp")
    private String numberWhatsapp;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "base_value")
    private Double baseValue;

    @Column(name = "additional_value")
    private Double additionalValue;

    @Column(name = "status")
    private String status;

    @Column(name = "external_company_id")
    private Long externalCompanyId;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "rp_integration_id")
    private String rpIntegrationId;

    @Column(name = "number_id")
    private String numberId;

    @Column(name = "token_meta")
    private String tokenMeta;

    @Column(name = "number_bot_mesa")
    private String numberBotMesa;

    @Column(name = "number_bot_delivery")
    private String numberBotDelivery;

    @Column(name = "STATUS_INTEGRATION_RP")
    private String statusIntegrationRp;

    @Column(name = "token_meta_delivery")
    private String tokenMetaDelivery;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
