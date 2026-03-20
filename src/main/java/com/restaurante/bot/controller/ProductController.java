package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.ProductUseCase;
import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductUpdateDTO;
import com.restaurante.bot.model.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/product")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class ProductController {

    private final ProductUseCase productInterface;

    @GetMapping("/getProductByCompany/{companyExternalId}")
    public ResponseEntity<CategorizedProductsDTO> getProductsSfotRestaurantByCompanyId(@PathVariable("companyExternalId") Long companyExternalId) {
        return new ResponseEntity<>(productInterface.getProductsSfotRestaurantByCompanyId(companyExternalId), HttpStatus.OK) ;
    }

    @PostMapping("/update-data")
    public ResponseEntity<GenericResponse>updateOrCreateProductsWithCategory(@RequestParam("companyExternalId") Long companyExternalId){
        log.info("Se inicia el servicio que nos va a crear o " +
                "actualizar los datos de los productos de la compañia -> {}", companyExternalId);
        return new ResponseEntity<>(productInterface.updateOrCreateProductsWithCategory(companyExternalId), HttpStatus.OK) ;
    }


    @PutMapping("/updateDescription")
    public ResponseEntity<ProductDto> updateProductDescription(@RequestBody ProductUpdateDTO productUpdateDTO) {
        ProductDto updatedProduct = productInterface.updateProductDescription(productUpdateDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(
            @RequestParam("companyExternalId") Long companyExternalId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, name = "category") String categoryName
    ) {
        List<ProductDto> result = productInterface.searchProducts(companyExternalId, name, categoryName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/by-price")
    public ResponseEntity<List<ProductDto>> listByPrice(
            @RequestParam("companyExternalId") Long companyExternalId,
            @RequestParam(required = false, name = "category") String categoryName,
            @RequestParam(required = false, name = "sort") String sort,
            @RequestParam(required = false, name = "name") String name
    ) {
        List<ProductDto> result = productInterface.listByPrice(companyExternalId, categoryName, sort, name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
