package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.ProductCrudUseCase;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductGetAllDto;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.model.ProductComment;
import com.restaurante.bot.repository.ProductRepository;
import com.restaurante.bot.repository.ProductCommentRepository;
import com.restaurante.bot.repository.CommentRepository;
import com.restaurante.bot.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductCrudUseCaseImpl implements ProductCrudUseCase {

    private final ProductRepository productRepository;
    private final ProductCommentRepository productCommentRepository;
    private final CommentRepository commentRepository;

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
        if (productDto.getComments() != null) {
            // persist comments in legacy JSON column for now
            try {
                String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(productDto.getComments());
                entity.setComments(json);
            } catch (Exception e) {
                entity.setComments("[]");
            }
        }

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
        if (productDto.getComments() != null) {
            try {
                String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(productDto.getComments());
                entity.setComments(json);
            } catch (Exception e) {
                entity.setComments("[]");
            }
        }

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
    public Page<ProductGetAllDto> getAll(Map<String, String> customQuery, Long companyId) {
        String orders = "ASC";
        String sortBy = "productId";
        int page = 0;
        int size = 5;
        if (customQuery.containsKey("orders")) orders = customQuery.get("orders");
        if (customQuery.containsKey("sortBy")) sortBy = customQuery.get("sortBy");
        if (customQuery.containsKey("page")) page = Integer.parseInt(customQuery.get("page"));
        if (customQuery.containsKey("size")) size = Integer.parseInt(customQuery.get("size"));

        if (companyId != null) {
            Map<String, String> copy = customQuery == null ? new HashMap<>() : new HashMap<>(customQuery);
            copy.put("companyId", String.valueOf(companyId));
            return searchCustom(copy, companyId);
        }

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
    public Page<ProductGetAllDto> getAll(int page, int size, String orders, String sortBy, Long companyId) {
        if (companyId != null) {
            Map<String, String> query = new HashMap<>();
            query.put("companyId", String.valueOf(companyId));
            query.put("page", String.valueOf(page));
            query.put("size", String.valueOf(size));
            query.put("orders", orders);
            query.put("sortBy", sortBy);
            return searchCustom(query, companyId);
        }

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
    public List<ProductGetAllDto> getAllWithOutPage(Map<String, String> customQuery, Long companyId) {
        String status = null;
        if (customQuery != null && customQuery.containsKey("status")) status = customQuery.get("status");
        else status = Constants.ACTIVE_STATUS;

        if (companyId != null) {
            List<Product> found = productRepository.search(companyId, null, null);
            String finalStatus = status;
            return found.stream()
                    .filter(p -> finalStatus == null || finalStatus.equals(p.getStatus()))
                    .map(this::mapToGetAllDto)
                    .collect(Collectors.toList());
        }

        List<Product> all = productRepository.findAll();
        String finalStatus1 = status;
        return all.stream()
                .filter(p -> finalStatus1 == null || finalStatus1.equals(p.getStatus()))
                .map(this::mapToGetAllDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductGetAllDto> searchCustom(Map<String, String> customQuery, Long companyId) {
        String orders = "ASC";
        String sortBy = "productId";
        int page = 0;
        int size = 5;

        String name = null;
        Long categoryId = null;

        if (customQuery != null && customQuery.containsKey("name")) name = customQuery.get("name");
        if (customQuery != null && customQuery.containsKey("categoryId")) categoryId = Long.valueOf(customQuery.get("categoryId"));
        if (customQuery != null && customQuery.containsKey("orders")) orders = customQuery.get("orders");
        if (customQuery != null && customQuery.containsKey("sortBy")) sortBy = customQuery.get("sortBy");
        if (customQuery != null && customQuery.containsKey("page")) page = Integer.parseInt(customQuery.get("page"));
        if (customQuery != null && customQuery.containsKey("size")) size = Integer.parseInt(customQuery.get("size"));

        if (companyId != null) {
            List<Product> found = productRepository.search(companyId, name, categoryId);
            int from = Math.min(page * size, found.size());
            int to = Math.min(from + size, found.size());
            List<ProductGetAllDto> content = found.subList(from, to).stream().map(this::mapToGetAllDto).collect(Collectors.toList());
            return new PageImpl<>(content, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orders), sortBy)), found.size());
        }

        return getAll(page, size, orders, sortBy, null);
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
        // First try normalized product_comments -> comments
        java.util.List<String> commentsList = new java.util.ArrayList<>();
        try {
            java.math.BigDecimal pid = java.math.BigDecimal.valueOf(product.getProductId());
            java.util.List<com.restaurante.bot.model.ProductComment> pcs = productCommentRepository.findByProductId(pid);
            if (pcs != null && !pcs.isEmpty()) {
                for (com.restaurante.bot.model.ProductComment pc : pcs) {
                    if (pc.getComment() != null && pc.getComment().getText() != null) {
                        commentsList.add(pc.getComment().getText());
                    } else if (pc.getCommentId() != null) {
                        commentRepository.findById(pc.getCommentId()).ifPresent(c -> {
                            if (c.getText() != null) commentsList.add(c.getText());
                        });
                    }
                }
            }
        } catch (Exception ignored) {
            // fallback to legacy column
        }

        if (commentsList.isEmpty() && product.getComments() != null && !product.getComments().trim().isEmpty()) {
            String raw = product.getComments();
            try {
                java.util.List<String> list = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(raw, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});
                commentsList.addAll(list);
            } catch (Exception ex) {
                java.util.List<String> list = java.util.Arrays.stream(raw.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(java.util.stream.Collectors.toList());
                commentsList.addAll(list);
            }
        }
        dto.setComments(commentsList);
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
        dto.setDescription(product.getDescription());
        dto.setImage(product.getImgProduct());
        java.util.List<String> commentsList = new java.util.ArrayList<>();
        try {
            java.math.BigDecimal pid = java.math.BigDecimal.valueOf(product.getProductId());
            java.util.List<com.restaurante.bot.model.ProductComment> pcs = productCommentRepository.findByProductId(pid);
            if (pcs != null && !pcs.isEmpty()) {
                for (com.restaurante.bot.model.ProductComment pc : pcs) {
                    if (pc.getComment() != null && pc.getComment().getText() != null) {
                        commentsList.add(pc.getComment().getText());
                    } else if (pc.getCommentId() != null) {
                        commentRepository.findById(pc.getCommentId()).ifPresent(c -> {
                            if (c.getText() != null) commentsList.add(c.getText());
                        });
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (commentsList.isEmpty() && product.getComments() != null && !product.getComments().trim().isEmpty()) {
            String raw = product.getComments();
            try {
                java.util.List<String> list = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(raw, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});
                commentsList.addAll(list);
            } catch (Exception ex) {
                java.util.List<String> list = java.util.Arrays.stream(raw.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(java.util.stream.Collectors.toList());
                commentsList.addAll(list);
            }
        }
        dto.setComments(commentsList);
        dto.setInformation(product.getInformation());
        dto.setPreparationTime(product.getPreparationTime());
        return dto;
    }
}
