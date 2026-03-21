package com.restaurante.bot.controller;


import com.restaurante.bot.application.ports.incoming.CompanyUseCase;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.restaurante.bot.exception.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "Company", description = "APIs para la gestión de compañías")
@RestController
@RequestMapping("/${app.request.mapping}/company")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@Slf4j
public class CompanyController {

    private final CompanyUseCase companyUseCase;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyRequest> save(
            @RequestPart("company") @Valid CompanyRequest companyRequest,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) {
        validateCreateCompanyRequest(companyRequest);
        CompanyRequest savedCompany = companyUseCase.save(companyRequest, logo);
        return ResponseEntity.ok(savedCompany);
    }


    @GetMapping("/get-company")
    public ResponseEntity<List<CompanyRequest>> getAllCategories() {
        log.info("Iniciando endpoint para obtener todas las compañias");
        List<CompanyRequest> companys = companyUseCase.getAllCompany();
        return new ResponseEntity<>(companys, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyRequest> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(companyUseCase.get(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        Boolean result = companyUseCase.delete(id);
        if (result != null && result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/update/{companyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyRequest> update(@PathVariable Long companyId,
                                                 @RequestPart("company") @Valid CompanyRequest companyRequest,
                                                 @RequestPart(value = "logo", required = false) MultipartFile logo) {
        log.info("Iniciando actualización de empresa con ID: {}", companyId);
        companyRequest.setCompanyId(companyId);
        validateUpdateCompanyRequest(companyRequest);
        CompanyRequest updatedCompany = companyUseCase.update(companyRequest, logo);
        return ResponseEntity.ok(updatedCompany);
    }

    private void validateCreateCompanyRequest(CompanyRequest companyRequest) {
        List<String> missingFields = new ArrayList<>();

        addMissingField(missingFields, "companyName", companyRequest.getNameCompany());
        addMissingField(missingFields, "longitude", companyRequest.getLongitude());
        addMissingField(missingFields, "latitude", companyRequest.getLatitude());
        addMissingField(missingFields, "apiKey", companyRequest.getApiKey());

        if (companyRequest.getBaseValue() == null) {
            missingFields.add("baseValue");
        }
        if (companyRequest.getAdditionalValue() == null) {
            missingFields.add("additionalValue");
        }
        if (companyRequest.getCityId() == null) {
            missingFields.add("cityId");
        }

        throwIfMissingFields(missingFields);
    }

    private void validateUpdateCompanyRequest(CompanyRequest companyRequest) {
        List<String> missingFields = new ArrayList<>();
        addMissingField(missingFields, "apiKey", companyRequest.getApiKey());
        throwIfMissingFields(missingFields);
    }

    private void addMissingField(List<String> missingFields, String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            missingFields.add(fieldName);
        }
    }

    private void throwIfMissingFields(List<String> missingFields) {
        if (!missingFields.isEmpty()) {
            throw new GenericException("Campos obligatorios faltantes: " + String.join(", ", missingFields), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<CompanyResponseDTO>> getAll(@RequestParam Map<String, String> customQuery) {
        return new ResponseEntity<>(companyUseCase.getAll(customQuery), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<CompanyResponseDTO>> getAll(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "ASC") String orders,
                                                           @RequestParam(defaultValue = "id") String sortBy) {
        return new ResponseEntity<>(companyUseCase.getAllPageCompany(page, size, orders, sortBy), HttpStatus.OK);
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<CompanyResponseDTO>> getAllWithoutPage(@RequestParam Map<String, String> customQuery) {
        return new ResponseEntity<>(companyUseCase.getAllWithoutPage(customQuery), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CompanyResponseDTO>> search(@RequestParam Map<String, String> customQuery) {
        return new ResponseEntity<>(companyUseCase.searchCustom(customQuery), HttpStatus.OK);
    }


}
