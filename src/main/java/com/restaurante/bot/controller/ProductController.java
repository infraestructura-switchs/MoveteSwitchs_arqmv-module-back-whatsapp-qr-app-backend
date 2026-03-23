package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.ProductUseCase;
import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductUpdateDTO;
import com.restaurante.bot.model.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${app.request.mapping}/product")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class ProductController {

    private final ProductUseCase productInterface;

    @GetMapping("/getProductByCompany/{externalCompanyId}")
    public ResponseEntity<Map<String, Object>> getProductsSfotRestaurantByCompanyId(
            @PathVariable("externalCompanyId") Long externalCompanyId,
            @RequestParam(name = "format", defaultValue = "categories") String format) {
        CategorizedProductsDTO categorizedProducts = productInterface.getProductsSfotRestaurantByCompanyId(externalCompanyId);
        Map<String, List<ProductDto>> productsByCategory = buildProductsByCategory(categorizedProducts);

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("companyId", externalCompanyId);
        meta.put("fetchedAt", Instant.now().toString());
        meta.put("totalProducts", productsByCategory.values().stream().mapToInt(List::size).sum());

        Map<String, Object> response = new LinkedHashMap<>();
        String normalizedFormat = format == null ? "categories" : format.trim().toLowerCase();

        switch (normalizedFormat) {
            case "map":
                response.put("productsByCategory", productsByCategory);
                break;
            case "both":
                response.put("categories", categorizedProducts.getCategories());
                response.put("productsByCategory", productsByCategory);
                break;
            case "categories":
            default:
                response.put("categories", categorizedProducts.getCategories());
                break;
        }

        response.put("meta", meta);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Map<String, List<ProductDto>> buildProductsByCategory(CategorizedProductsDTO categorizedProducts) {
        Map<String, List<ProductDto>> productsByCategory = new LinkedHashMap<>();
        categorizedProducts.getCategories().forEach(category ->
            productsByCategory.put(category.getCategoryName(), category.getProducts())
        );
        return productsByCategory;
    }

    @PostMapping("/update-data")
    public ResponseEntity<GenericResponse>updateOrCreateProductsWithCategory(@RequestParam("externalCompanyId") Long externalCompanyId){
        log.info("Se inicia el servicio que nos va a crear o " +
                "actualizar los datos de los productos de la compañia -> {}", externalCompanyId);
        return new ResponseEntity<>(productInterface.updateOrCreateProductsWithCategory(externalCompanyId), HttpStatus.OK) ;
    }


    @PutMapping("/updateDescription")
    public ResponseEntity<ProductDto> updateProductDescription(@RequestBody ProductUpdateDTO productUpdateDTO) {
        ProductDto updatedProduct = productInterface.updateProductDescription(productUpdateDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(
            @RequestParam("externalCompanyId") Long externalCompanyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, name = "category") String categoryName
    ) {
        List<ProductDto> result = productInterface.searchProducts(externalCompanyId, name, categoryName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/by-price")
    public ResponseEntity<List<ProductDto>> listByPrice(
            @RequestParam("externalCompanyId") Long externalCompanyId,
            @RequestParam(required = false, name = "category") String categoryName,
            @RequestParam(required = false, name = "sort") String sort,
            @RequestParam(required = false, name = "name") String name
    ) {
        List<ProductDto> result = productInterface.listByPrice(externalCompanyId, categoryName, sort, name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
