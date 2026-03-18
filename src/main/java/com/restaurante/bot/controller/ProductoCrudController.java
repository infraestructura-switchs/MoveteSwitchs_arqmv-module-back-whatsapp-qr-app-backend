package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.ProductCrudUseCase;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductGetAllDto;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import com.restaurante.bot.exception.GenericException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "Producto", description = "APIs para la gestión de productos (CRUD)")
@RestController
@RequestMapping({"/${app.request.mapping}/admin/product", "/api/back-whatsapp-qr-app/producto"})
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RequiredArgsConstructor
@Slf4j
public class ProductoCrudController {

    private final ProductCrudUseCase productCrudUseCase;

    @PostMapping("/create")
    public ResponseEntity<ProductDto> save(@RequestBody @Valid ProductSaveAndUpdateDto productDto) {
        validateProductRequest(productDto);
        return ResponseEntity.ok(productCrudUseCase.save(productDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(productCrudUseCase.get(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable("id") Long productId,
            @RequestBody @Valid ProductSaveAndUpdateDto productDto) {
        validateProductRequest(productDto);
        return ResponseEntity.ok(productCrudUseCase.update(productId, productDto));
    }

    private void validateProductRequest(ProductSaveAndUpdateDto productDto) {
        List<String> missingFields = new ArrayList<>();
        List<String> invalidFields = new ArrayList<>();

        if (productDto.getProductName() == null || productDto.getProductName().trim().isEmpty()) {
            missingFields.add("productName");
        }
        if (productDto.getPrice() == null) {
            missingFields.add("price");
        } else if (productDto.getPrice() <= 0) {
            invalidFields.add("price");
        }
        if (productDto.getCompanyId() == null) {
            missingFields.add("companyId");
        } else if (productDto.getCompanyId() <= 0) {
            invalidFields.add("companyId");
        }
        if (productDto.getCategoryId() == null) {
            missingFields.add("categoryId");
        } else if (productDto.getCategoryId() <= 0) {
            invalidFields.add("categoryId");
        }
        if (productDto.getPreparationTime() == null) {
            missingFields.add("preparationTime");
        } else if (productDto.getPreparationTime() <= 0) {
            invalidFields.add("preparationTime");
        }

        if (!missingFields.isEmpty()) {
            throw new GenericException("Campos obligatorios faltantes: " + String.join(", ", missingFields), HttpStatus.BAD_REQUEST);
        }

        if (!invalidFields.isEmpty()) {
            throw new GenericException("Campos con valor invalido: " + String.join(", ", invalidFields), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        boolean result = productCrudUseCase.delete(id);
        if (result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<ProductGetAllDto>> getAll(@RequestParam Map<String, String> customQuery,
            @RequestParam("companyId") Long companyId) {
        return ResponseEntity.ok(productCrudUseCase.getAll(customQuery, companyId));
    }

    @GetMapping
    public ResponseEntity<Page<ProductGetAllDto>> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ASC") String orders,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam("companyId") Long companyId) {
        return ResponseEntity.ok(productCrudUseCase.getAll(page, size, orders, sortBy, companyId));
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<ProductGetAllDto>> getAllWithoutPage(@RequestParam Map<String, String> customQuery,
            @RequestParam("companyId") Long companyId) {
        return ResponseEntity.ok(productCrudUseCase.getAllWithOutPage(customQuery, companyId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductGetAllDto>> search(@RequestParam Map<String, String> customQuery,
                                                         @RequestParam("companyId") Long companyId) {
        return ResponseEntity.ok(productCrudUseCase.searchCustom(customQuery, companyId));
    }
}
