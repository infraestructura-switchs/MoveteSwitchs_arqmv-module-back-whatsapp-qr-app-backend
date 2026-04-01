package com.restaurante.bot.config;

import com.restaurante.bot.business.service.ProductIntegrationSyncService;
import com.restaurante.bot.model.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductIntegrationScheduler {

    private final ProductIntegrationSyncService productIntegrationSyncService;

    /**
     * Job programado que ejecuta la actualización de productos para todas las compañías
     * Se ejecuta diariamente a las 7:00 AM
     * Expresión CRON: 0 00 07 * * ? (7:00 AM cada día)
     */
    @Scheduled(cron = "0 00 07 * * ?")
    public void syncAllProductsJob() {
        try {
            log.info("Iniciando job programado de sincronizacion ProductIntegration para todas las companias");
            GenericResponse response = productIntegrationSyncService.syncAllCompanies();
            log.info("Job completado exitosamente: {}", response.getMessage());
        } catch (Exception e) {
            log.error("Error en el job de sincronización de productos", e);
        }
    }
}
