package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.ProductDiscountCrudUseCase;
import com.restaurante.bot.dto.ProductDiscountDto;
import com.restaurante.bot.dto.ProductDiscountSaveAndUpdateDto;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "Descuentos de Producto", description = "APIs para la gestion de descuentos por producto")
@RestController
@RequestMapping("/${app.request.mapping}/admin/product-discount")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RequiredArgsConstructor
public class ProductDiscountController {

    private final ProductDiscountCrudUseCase productDiscountCrudUseCase;

    @PostMapping("/create")
    public ResponseEntity<ProductDiscountDto> save(@RequestBody @Valid ProductDiscountSaveAndUpdateDto request) {
        return ResponseEntity.ok(productDiscountCrudUseCase.save(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDiscountDto> get(@PathVariable Long id, @RequestParam Long companyId) {
        return ResponseEntity.ok(productDiscountCrudUseCase.get(id, companyId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDiscountDto> update(@PathVariable Long id,
                                                     @RequestBody @Valid ProductDiscountSaveAndUpdateDto request) {
        return ResponseEntity.ok(productDiscountCrudUseCase.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id, @RequestParam Long companyId) {
        boolean deleted = productDiscountCrudUseCase.delete(id, companyId);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<ProductDiscountDto>> getAll(@RequestParam Map<String, String> filters,
                                                           @RequestParam("companyId") Long companyId) {
        return ResponseEntity.ok(productDiscountCrudUseCase.getAll(filters, companyId));
    }

    

}