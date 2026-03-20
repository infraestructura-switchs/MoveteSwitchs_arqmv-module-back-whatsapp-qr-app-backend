package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "company")
public class Company {
    @Id
    @Column(name = "company_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "logo")
    private String logo;

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

    @Column(name = "STATUS_INTEGRATION_RP")
    private String statusIntegrationRp;

    @Column(name = "landing_template")
    private String landingTemplate;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CompanyIntegrationDetail> integrationDetails = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getNumberWhatsapp() {
        return integrationDetail() != null ? integrationDetail().getNumberWhatsapp() : null;
    }

    public void setNumberWhatsapp(String numberWhatsapp) {
        ensureIntegrationDetail().setNumberWhatsapp(numberWhatsapp);
    }

    public String getNumberId() {
        return integrationDetail() != null ? integrationDetail().getNumberId() : null;
    }

    public void setNumberId(String numberId) {
        ensureIntegrationDetail().setNumberId(numberId);
    }

    public String getTokenMeta() {
        return integrationDetail() != null ? integrationDetail().getTokenMeta() : null;
    }

    public void setTokenMeta(String tokenMeta) {
        ensureIntegrationDetail().setTokenMeta(tokenMeta);
    }

    public String getNumberBotMesa() {
        return integrationDetail() != null ? integrationDetail().getNumberBotMesa() : null;
    }

    public void setNumberBotMesa(String numberBotMesa) {
        ensureIntegrationDetail().setNumberBotMesa(numberBotMesa);
    }

    public String getNumberBotDelivery() {
        return integrationDetail() != null ? integrationDetail().getNumberBotDelivery() : null;
    }

    public void setNumberBotDelivery(String numberBotDelivery) {
        ensureIntegrationDetail().setNumberBotDelivery(numberBotDelivery);
    }

    public String getTokenMetaDelivery() {
        return integrationDetail() != null ? integrationDetail().getTokenMetaDelivery() : null;
    }

    public void setTokenMetaDelivery(String tokenMetaDelivery) {
        ensureIntegrationDetail().setTokenMetaDelivery(tokenMetaDelivery);
    }

    private CompanyIntegrationDetail integrationDetail() {
        if (integrationDetails == null || integrationDetails.isEmpty()) {
            return null;
        }
        return integrationDetails.get(0);
    }

    private CompanyIntegrationDetail ensureIntegrationDetail() {
        CompanyIntegrationDetail detail = integrationDetail();
        if (detail == null) {
            detail = new CompanyIntegrationDetail();
            detail.setCompany(this);
            integrationDetails.add(detail);
        }
        return detail;
    }

}
