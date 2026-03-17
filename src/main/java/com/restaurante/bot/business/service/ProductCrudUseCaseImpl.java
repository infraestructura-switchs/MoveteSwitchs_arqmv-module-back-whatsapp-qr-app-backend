package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.ProductCrudUseCase;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductGetAllDto;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.repository.ProductRepository;
import com.restaurante.bot.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductCrudUseCaseImpl implements ProductCrudUseCase {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductDto save(ProductSaveAndUpdateDto productDto) {
        Product entity = new Product();
        // productId left null to allow DB to generate if applicable
        entity.setName(productDto.getProductName());
        entity.setPrice(productDto.getPrice());
        entity.setDescription(productDto.getDescription());
        entity.setStatus(productDto.getStatus() == null ? Constants.ACTIVE_STATUS : productDto.getStatus());
        entity.setImgProduct(productDto.getImage());
        entity.setCategoryId(productDto.getCategoryId());
        entity.setInformation(productDto.getInformation());
        entity.setPreparationTime(productDto.getPreparationTime());

        Product saved = productRepository.save(entity);
        return mapToDto(saved);
    }

    @Override
    public ProductDto get(Long id) {
        Optional<Product> opt = productRepository.findById(id);
        if (opt.isPresent()) return mapToDto(opt.get());
        throw new RuntimeException("Producto no encontrado");
    }

    @Override
    @Transactional
    public ProductDto update(Long productId, ProductSaveAndUpdateDto productDto) {
        Optional<Product> opt = productRepository.findById(productId);
        if (!opt.isPresent()) throw new RuntimeException("Producto no existe");
        Product entity = opt.get();
        if (productDto.getProductName() != null) entity.setName(productDto.getProductName());
        if (productDto.getPrice() != null) entity.setPrice(productDto.getPrice());
        if (productDto.getDescription() != null) entity.setDescription(productDto.getDescription());
        if (productDto.getStatus() != null) entity.setStatus(productDto.getStatus());
        if (productDto.getImage() != null) entity.setImgProduct(productDto.getImage());
        if (productDto.getCategoryId() != null) entity.setCategoryId(productDto.getCategoryId());
        if (productDto.getInformation() != null) entity.setInformation(productDto.getInformation());
        if (productDto.getPreparationTime() != null) entity.setPreparationTime(productDto.getPreparationTime());

        Product updated = productRepository.save(entity);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Optional<Product> opt = productRepository.findById(id);
        if (opt.isPresent()) {
            Product p = opt.get();
            p.setStatus(Constants.INACTIVE_STATUS);
            productRepository.save(p);
            return true;
        }
        return false;
    }

    @Override
    public Page<ProductGetAllDto> getAll(Map<String, String> customQuery) {
        String orders = "ASC";
        String sortBy = "productId";
        int page = 0;
        int size = 5;

        if (customQuery.containsKey("orders")) orders = customQuery.get("orders");
        if (customQuery.containsKey("sortBy")) sortBy = customQuery.get("sortBy");
        if (customQuery.containsKey("page")) page = Integer.parseInt(customQuery.get("page"));
        if (customQuery.containsKey("size")) size = Integer.parseInt(customQuery.get("size"));

        Sort.Direction direction = Sort.Direction.fromString(orders);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Product> entityPage = productRepository.findAll(pagingSort);
        int total = (int) entityPage.getTotalElements();
        List<ProductGetAllDto> list = entityPage.getContent().stream()
                .map(this::mapToGetAllDto)
                .collect(Collectors.toList());
        return new PageImpl<>(list, pagingSort, total);
    }

    @Override
    public Page<ProductGetAllDto> getAll(int page, int size, String orders, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(orders);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Product> entityPage = productRepository.findAll(pagingSort);
        int total = (int) entityPage.getTotalElements();
        List<ProductGetAllDto> list = entityPage.getContent().stream()
                .map(this::mapToGetAllDto)
                .collect(Collectors.toList());
        return new PageImpl<>(list, pagingSort, total);
    }

    @Override
    public List<ProductGetAllDto> getAllWithOutPage(Map<String, String> customQuery) {
        String status;
        if (customQuery.containsKey("status")) status = customQuery.get("status");
        else {
            status = Constants.ACTIVE_STATUS;
        }

        List<Product> all = productRepository.findAll();
        return all.stream()
                .filter(p -> status == null || status.equals(p.getStatus()))
                .map(this::mapToGetAllDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductGetAllDto> searchCustom(Map<String, String> customQuery) {
        String orders = "ASC";
        String sortBy = "productId";
        int page = 0;
        int size = 5;

        Long companyId = null;
        String name = null;
        Long categoryId = null;

        if (customQuery.containsKey("companyId")) companyId = Long.valueOf(customQuery.get("companyId"));
        if (customQuery.containsKey("name")) name = customQuery.get("name");
        if (customQuery.containsKey("categoryId")) categoryId = Long.valueOf(customQuery.get("categoryId"));
        if (customQuery.containsKey("orders")) orders = customQuery.get("orders");
        if (customQuery.containsKey("sortBy")) sortBy = customQuery.get("sortBy");
        if (customQuery.containsKey("page")) page = Integer.parseInt(customQuery.get("page"));
        if (customQuery.containsKey("size")) size = Integer.parseInt(customQuery.get("size"));

        if (companyId != null) {
            List<Product> found = productRepository.search(companyId, name, categoryId);
            int from = Math.min(page * size, found.size());
            int to = Math.min(from + size, found.size());
            List<ProductGetAllDto> content = found.subList(from, to).stream().map(this::mapToGetAllDto).collect(Collectors.toList());
            return new PageImpl<>(content, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orders), sortBy)), found.size());
        }

        return getAll(page, size, orders, sortBy);
    }

    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getProductId());
        dto.setProductName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setStatus(product.getStatus());
        dto.setImage(product.getImgProduct());
        dto.setCategoryId(product.getCategoryId());
        dto.setInformation(product.getInformation());
        dto.setPreparationTime(product.getPreparationTime());
        return dto;
    }

    private ProductGetAllDto mapToGetAllDto(Product product) {
        ProductGetAllDto dto = new ProductGetAllDto();
        dto.setId(product.getProductId());
        dto.setProductName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStatus(product.getStatus());
        dto.setCategoryId(product.getCategoryId());
        return dto;
    }
}
