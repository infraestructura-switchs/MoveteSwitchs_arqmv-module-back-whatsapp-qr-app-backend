package com.restaurante.bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_integration_detail")
public class CompanyIntegrationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_integration_detail_id")
    private Long companyIntegrationDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "number_whatsapp")
    private String numberWhatsapp;

    @Column(name = "number_id")
    private String numberId;

    @Column(name = "token_meta")
    private String tokenMeta;

    @Column(name = "number_bot_delivery")
    private String numberBotDelivery;

    @Column(name = "number_bot_mesa")
    private String numberBotMesa;

    @Column(name = "status_integration_rp")
    private String statusIntegrationRp;

    @Column(name = "token_meta_delivery")
    private String tokenMetaDelivery;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
