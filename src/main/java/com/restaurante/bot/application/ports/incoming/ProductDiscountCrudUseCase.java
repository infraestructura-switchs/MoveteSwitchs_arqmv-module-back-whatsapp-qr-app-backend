package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.dto.ProductDiscountCreateDto;
import com.restaurante.bot.dto.ProductDiscountDto;
import com.restaurante.bot.dto.ProductDiscountSaveAndUpdateDto;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ProductDiscountCrudUseCase {

    ProductDiscountDto save(ProductDiscountCreateDto productDiscountDto);

    ProductDiscountDto get(Long id, Long companyId);

    ProductDiscountDto update(Long id, ProductDiscountSaveAndUpdateDto productDiscountDto);

    boolean delete(Long id, Long companyId);

    Page<ProductDiscountDto> getAll(Map<String, String> filters, Long companyId);
}