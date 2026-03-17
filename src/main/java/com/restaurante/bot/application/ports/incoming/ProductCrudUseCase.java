package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductGetAllDto;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ProductCrudUseCase {

    ProductDto save(ProductSaveAndUpdateDto productDto);

    ProductDto get(Long id);

    ProductDto update(Long productId, ProductSaveAndUpdateDto productDto);

    boolean delete(Long id);
    Page<ProductGetAllDto> getAll(Map<String, String> customQuery, Long companyId);

    Page<ProductGetAllDto> getAll(int page, int size, String orders, String sortBy, Long companyId);

    List<ProductGetAllDto> getAllWithOutPage(Map<String, String> customQuery, Long companyId);

    Page<ProductGetAllDto> searchCustom(Map<String, String> customQuery, Long companyId);
}
