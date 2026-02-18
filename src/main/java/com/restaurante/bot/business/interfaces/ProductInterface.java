package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductResponseDTO;
import com.restaurante.bot.dto.ProductUpdateDTO;
import com.restaurante.bot.model.GenericResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ProductInterface {

    CategorizedProductsDTO getProductsSfotRestaurantByCompanyId(Long companyId);

    GenericResponse updateOrCreateProductsWithCategory(Long companyId);

    ProductDto updateProductDescription(ProductUpdateDTO productUpdateDTO);

    List<ProductDto> searchProducts(Long companyId, String name, String categoryName);

    List<ProductDto> listByPrice(Long companyId, String categoryName, String sort,String name);

}
