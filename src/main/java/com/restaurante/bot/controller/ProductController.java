package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.ProductInterface;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class ProductController {

    private final ProductInterface productInterface;

    @GetMapping("/getProductByCompany/{companyId}")
    public ResponseEntity<CategorizedProductsDTO> getProductsSfotRestaurantByCompanyId(@PathVariable Long companyId) {
        return new ResponseEntity<>(productInterface.getProductsSfotRestaurantByCompanyId(companyId), HttpStatus.OK) ;
    }

    @PostMapping("/update-data")
    public ResponseEntity<GenericResponse>updateOrCreateProductsWithCategory(@RequestParam Long companyId){
        log.info("Se inicia el servicio que nos va a crear o " +
                "actualizar los datos de los productos de la compañia -> {}", companyId);
        return new ResponseEntity<>(productInterface.updateOrCreateProductsWithCategory(companyId), HttpStatus.OK) ;
    }


    @PutMapping("/updateDescription")
    public ResponseEntity<ProductDto> updateProductDescription(@RequestBody ProductUpdateDTO productUpdateDTO) {
        ProductDto updatedProduct = productInterface.updateProductDescription(productUpdateDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(
            @RequestParam Long companyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, name = "category") String categoryName
    ) {
        List<ProductDto> result = productInterface.searchProducts(companyId, name, categoryName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/by-price")
    public ResponseEntity<List<ProductDto>> listByPrice(
            @RequestParam Long companyId,
            @RequestParam(required = false, name = "category") String categoryName,
            @RequestParam(required = false, name = "sort") String sort,
            @RequestParam(required = false, name = "name") String name
    ) {
        List<ProductDto> result = productInterface.listByPrice(companyId, categoryName, sort, name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
