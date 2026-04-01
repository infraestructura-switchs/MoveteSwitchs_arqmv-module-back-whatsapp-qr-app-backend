package com.restaurante.bot.controller;

import com.restaurante.bot.business.service.ProductIntegrationSyncService;
import com.restaurante.bot.model.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${app.request.mapping}/product-integration")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ProductIntegrationController {

    private final ProductIntegrationSyncService productIntegrationSyncService;

    @PostMapping("/update-data")
    public ResponseEntity<GenericResponse> syncCompany(@RequestParam("externalCompanyId") Long externalCompanyId) {
        log.info("Iniciando sincronizacion manual ProductIntegration para la compania {}", externalCompanyId);
        GenericResponse response = productIntegrationSyncService.syncCompany(externalCompanyId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/sync-all-companies")
    public ResponseEntity<GenericResponse> syncAllCompanies() {
        log.info("Iniciando sincronizacion manual ProductIntegration para todas las companias");
        GenericResponse response = productIntegrationSyncService.syncAllCompanies();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
