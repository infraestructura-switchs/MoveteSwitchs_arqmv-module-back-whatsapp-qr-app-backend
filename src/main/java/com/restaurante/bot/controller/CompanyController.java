package com.restaurante.bot.controller;


import com.restaurante.bot.business.interfaces.CompanyInterface;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/company")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@Slf4j
public class CompanyController {

    private final CompanyInterface companyService;

    @PostMapping("/create")
    public ResponseEntity<CompanyRequest> createCompany(
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "whatsappNumber", required = false) String whatsappNumber,
            @RequestParam(value = "longitude", required = false) String longitude,
            @RequestParam(value = "latitude", required = false) String latitude,
            @RequestParam(value = "baseValue", required = false) Double baseValue,
            @RequestParam(value = "additionalValue", required = false) Double additionalValue,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "externalId", required = false) Long externalId,
            @RequestParam(value = "cityId", required = false) Long cityId,
            @RequestParam(value = "apiKey") String apiKey,
            @RequestParam(value = "rappyId", required = false) String rappyId,
            @RequestParam(value = "numberId", required = false) String numberId,
            @RequestParam(value = "tokenMetaQr", required = false) String tokenMetaQr,
            @RequestParam(value = "numberBotMesa", required = false) String numberBotMesa,
            @RequestParam(value = "numberBotDelivery", required = false) String numberBotDelivery,
            @RequestParam(value = "tokenMetaDelivery", required = false) String tokenMetaDelivery
            ) {

        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setNameCompany(companyName);
        companyRequest.setNumberWhatsapp(whatsappNumber);
        companyRequest.setLongitude(longitude);
        companyRequest.setLatitude(latitude);
        companyRequest.setBaseValue(baseValue);
        companyRequest.setAdditionalValue(additionalValue);
        companyRequest.setExternalCompanyId(externalId);
        companyRequest.setCityId(cityId);
        companyRequest.setApiKey(apiKey);
        companyRequest.setRpIntegrationId(rappyId);
        companyRequest.setNumberId(numberId);
        companyRequest.setTokenMeta(tokenMetaQr);
        companyRequest.setTokenMetaDelivery(tokenMetaDelivery);
        companyRequest.setNumberBotMesa(numberBotMesa);
        companyRequest.setNumberBotDelivery(numberBotDelivery);


        CompanyRequest savedCompany = companyService.save(companyRequest, logo);

        return ResponseEntity.ok(savedCompany);
    }


    @GetMapping("/get-company")
    public ResponseEntity<List<CompanyRequest>> getAllCategories() {
        log.info("Iniciando endpoint para obtener todas las compañias");
        List<CompanyRequest> categories = companyService.getAllCompany();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateByCompanynId/{companyId}")
    public ResponseEntity<CompanyRequest> updateCompany(
            @PathVariable Long companyId,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "whatsappNumber", required = false) String whatsappNumber,
            @RequestParam(value = "longitude", required = false) String longitude,
            @RequestParam(value = "latitude", required = false) String latitude,
            @RequestParam(value = "baseValue", required = false) Double baseValue,
            @RequestParam(value = "additionalValue", required = false) Double additionalValue,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "externalId", required = false) Long externalId,
            @RequestParam(value = "cityId", required = false) Long cityId,
            @RequestParam(value = "apiKey") String apiKey,
            @RequestParam(value = "rappyId", required = false) String rappyId,
            @RequestParam(value = "numberId", required = false) String numberId,
            @RequestParam(value = "tokenMetaQr", required = false) String tokenMetaQr,
            @RequestParam(value = "numberBotMesa", required = false) String numberBotMesa,
            @RequestParam(value = "numberBotDelivery", required = false) String numberBotDelivery,
            @RequestParam(value = "tokenMetaDelivery", required = false) String tokenMetaDelivery) {

        log.info("Iniciando actualización de empresa con ID: {}", companyId);

        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setCompanyId(companyId);
        companyRequest.setNameCompany(companyName);
        companyRequest.setNumberWhatsapp(whatsappNumber);
        companyRequest.setLongitude(longitude);
        companyRequest.setLatitude(latitude);
        companyRequest.setBaseValue(baseValue);
        companyRequest.setAdditionalValue(additionalValue);
        companyRequest.setExternalCompanyId(externalId);
        companyRequest.setCityId(cityId);
        companyRequest.setApiKey(apiKey);
        companyRequest.setRpIntegrationId(rappyId);
        companyRequest.setNumberId(numberId);
        companyRequest.setTokenMeta(tokenMetaQr);
        companyRequest.setNumberBotMesa(numberBotMesa);
        companyRequest.setNumberBotDelivery(numberBotDelivery);

        CompanyRequest updatedCompany = companyService.update(companyRequest, logo);
        return ResponseEntity.ok(updatedCompany);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<CompanyResponseDTO>> getAllPageCompany(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "ASC") String orders,
                                                                  @RequestParam(defaultValue = "id") String sortBy){
        return new ResponseEntity<>(companyService.getAllPageCompany(page,size,orders,sortBy), HttpStatus.OK);

    }


}
