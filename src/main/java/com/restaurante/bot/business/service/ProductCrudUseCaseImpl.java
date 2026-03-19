package com.restaurante.bot.business.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.application.ports.incoming.ProductCrudUseCase;
import com.restaurante.bot.dto.CategoryResponseDTO;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductGetAllDto;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.model.ProductDiscount;
import com.restaurante.bot.repository.CategoryRepository;
import com.restaurante.bot.repository.CommentRepository;
import com.restaurante.bot.repository.ProductCommentRepository;
import com.restaurante.bot.repository.ProductRepository;
import com.restaurante.bot.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCrudUseCaseImpl implements ProductCrudUseCase {

    private final ProductRepository productRepository;
    private final ProductCommentRepository productCommentRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;
    private final ProductDiscountSupport productDiscountSupport;

    @Override
    @Transactional
    public ProductDto save(ProductSaveAndUpdateDto productDto) {
        Product entity = new Product();
        // productId left null to allow DB to generate if applicable
        entity.setName(productDto.getProductName());
        entity.setPrice(productDto.getPrice());
        if (productDto.getOriginalPrice() != null) entity.setOriginalPrice(productDto.getOriginalPrice());
        entity.setDescription(productDto.getDescription());
        entity.setStatus(productDto.getStatus() == null ? Constants.ACTIVE_STATUS : productDto.getStatus());
        entity.setImgProduct(productDto.getImage());
        entity.setCategoryId(productDto.getCategoryId());
        entity.setInformation(normalizeInformation(productDto.getInformation()));
        entity.setPreparationTime(productDto.getPreparationTime());
        entity.setCompanyId(productDto.getCompanyId());
        if (productDto.getComments() != null) {
            // persist comments in legacy JSON column for now
            try {
                String json = objectMapper.writeValueAsString(productDto.getComments());
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
        throw new GenericException("Producto no encontrado", HttpStatus.NOT_FOUND);
    }

    @Override
    @Transactional
    public ProductDto update(Long productId, ProductSaveAndUpdateDto productDto) {
        Optional<Product> opt = productRepository.findById(productId);
        if (!opt.isPresent()) throw new GenericException("Producto no existe", HttpStatus.NOT_FOUND);
        Product entity = opt.get();
        if (productDto.getProductName() != null) entity.setName(productDto.getProductName());
        if (productDto.getPrice() != null) entity.setPrice(productDto.getPrice());
        if (productDto.getOriginalPrice() != null) entity.setOriginalPrice(productDto.getOriginalPrice());
        if (productDto.getDescription() != null) entity.setDescription(productDto.getDescription());
        if (productDto.getStatus() != null) entity.setStatus(productDto.getStatus());
        if (productDto.getImage() != null) entity.setImgProduct(productDto.getImage());
        if (productDto.getCategoryId() != null) entity.setCategoryId(productDto.getCategoryId());
        if (productDto.getInformation() != null) {
            String normalizedInformation = normalizeInformation(productDto.getInformation());
            if (normalizedInformation != null) {
                entity.setInformation(normalizedInformation);
            }
        }
        if (productDto.getPreparationTime() != null) entity.setPreparationTime(productDto.getPreparationTime());
        // companyId is required for update
        entity.setCompanyId(productDto.getCompanyId());

        if (productDto.getComments() != null) {
            try {
                String json = objectMapper.writeValueAsString(productDto.getComments());
                entity.setComments(json);
            } catch (Exception e) {
                entity.setComments("[]");
            }
        }

        Product updated = productRepository.save(entity);
        return mapToDto(updated);
    }

    private String normalizeInformation(String information) {
        if (information == null) {
            return null;
        }

        String trimmedInformation = information.trim();
        if (trimmedInformation.isEmpty() || "[]".equals(trimmedInformation)) {
            return null;
        }

        return information;
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
        Map<Long, ProductDiscount> activeDiscounts = resolveActiveDiscounts(entityPage.getContent(), companyId);
        List<ProductGetAllDto> list = entityPage.getContent().stream()
            .map(product -> mapToGetAllDto(product, activeDiscounts.get(product.getProductId())))
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
        Map<Long, ProductDiscount> activeDiscounts = resolveActiveDiscounts(entityPage.getContent(), companyId);
        List<ProductGetAllDto> list = entityPage.getContent().stream()
            .map(product -> mapToGetAllDto(product, activeDiscounts.get(product.getProductId())))
                .collect(Collectors.toList());
        return new PageImpl<>(list, pagingSort, total);
    }

    @Override
    public List<ProductGetAllDto> getAllWithOutPage(Map<String, String> customQuery, Long companyId) {
        String status = null;
        if (customQuery != null && customQuery.containsKey("status")) status = customQuery.get("status");
        else status = Constants.ACTIVE_STATUS;

        if (companyId != null) {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 1000);
            org.springframework.data.domain.Page<Product> found = productRepository.search(companyId, null, null, pageable);
            String finalStatus = status;
            return found.getContent().stream()
                .filter(p -> finalStatus == null || finalStatus.equals(p.getStatus()))
                .map(product -> mapToGetAllDto(product, productDiscountSupport.findActiveDiscount(product.getCompanyId(), product.getProductId())))
                .collect(Collectors.toList());
        }

        org.springframework.data.domain.Pageable pageableAll = org.springframework.data.domain.PageRequest.of(0, 1000);
        org.springframework.data.domain.Page<Product> all = productRepository.findAll(pageableAll);
        String finalStatus1 = status;
        return all.getContent().stream()
            .filter(p -> finalStatus1 == null || finalStatus1.equals(p.getStatus()))
            .map(product -> mapToGetAllDto(product, productDiscountSupport.findActiveDiscount(product.getCompanyId(), product.getProductId())))
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
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orders), sortBy));
            org.springframework.data.domain.Page<Product> foundPage = productRepository.search(companyId, name, categoryId, pageable);
            Map<Long, ProductDiscount> activeDiscounts = resolveActiveDiscounts(foundPage.getContent(), companyId);
            java.util.List<ProductGetAllDto> content = foundPage.getContent().stream()
                    .map(product -> mapToGetAllDto(product, activeDiscounts.get(product.getProductId())))
                    .collect(Collectors.toList());
            return new PageImpl<>(content, pageable, foundPage.getTotalElements());
        }

        return getAll(page, size, orders, sortBy, null);
    }

    private ProductDto mapToDto(Product product) {
        ProductDiscount activeDiscount = productDiscountSupport.findActiveDiscount(product.getCompanyId(), product.getProductId());
        ProductDiscountSupport.ProductPriceSummary priceSummary = productDiscountSupport.summarize(
            product.getOriginalPrice() != null ? product.getOriginalPrice() : product.getPrice(),
            activeDiscount
        );
        ProductDto.ProductDtoBuilder dtoBuilder = ProductDto.builder()
            .id(product.getProductId())
            .productName(product.getName())
            .price(priceSummary.finalPrice())
            .originalPrice(priceSummary.originalPrice())
            .discountAmount(priceSummary.discountAmount())
            .description(product.getDescription())
            .status(product.getStatus())
            .image(product.getImgProduct())
            .categoryId(product.getCategoryId())
            .companyId(product.getCompanyId())
            .activeDiscount(productDiscountSupport.toDto(activeDiscount));
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
                java.util.List<String> list = objectMapper
                        .readValue(raw, new TypeReference<java.util.List<String>>() {});
                commentsList.addAll(list);
            } catch (Exception ex) {
                java.util.List<String> list = java.util.Arrays.stream(raw.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(java.util.stream.Collectors.toList());
                commentsList.addAll(list);
            }
        }
        dtoBuilder.comments(commentsList);
        dtoBuilder.information(product.getInformation());
        dtoBuilder.preparationTime(product.getPreparationTime());
        if (product.getCategoryId() != null) {
            try {
                categoryRepository.findById(product.getCategoryId()).ifPresent(cat -> {
                    CategoryResponseDTO catDto = CategoryResponseDTO.builder()
                            .categoryId(cat.getCategoryId())
                            .name(cat.getName())
                            .parameterId(cat.getExternalId())
                            .parameterName(null)
                            .status(cat.getStatus())
                            .companyId(cat.getCompanyId())
                            .createdAt(cat.getCreatedAt())
                            .updatedAt(cat.getUpdatedAt())
                            .build();
                    dtoBuilder.category(catDto);
                });
            } catch (Exception ignored) {}
        }
        return dtoBuilder.build();
    }

    private ProductGetAllDto mapToGetAllDto(Product product, ProductDiscount activeDiscount) {
        ProductDiscountSupport.ProductPriceSummary priceSummary = productDiscountSupport.summarize(
            product.getOriginalPrice() != null ? product.getOriginalPrice() : product.getPrice(),
            activeDiscount
        );
        ProductGetAllDto.ProductGetAllDtoBuilder dtoBuilder = ProductGetAllDto.builder()
            .id(product.getProductId())
            .productName(product.getName())
            .price(priceSummary.finalPrice())
            .originalPrice(priceSummary.originalPrice())
            .discountAmount(priceSummary.discountAmount())
            .status(product.getStatus())
            .categoryId(product.getCategoryId())
            .description(product.getDescription())
            .image(product.getImgProduct())
            .companyId(product.getCompanyId())
            .activeDiscount(productDiscountSupport.toDto(activeDiscount));
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
                java.util.List<String> list = objectMapper
                        .readValue(raw, new TypeReference<java.util.List<String>>() {});
                commentsList.addAll(list);
            } catch (Exception ex) {
                java.util.List<String> list = java.util.Arrays.stream(raw.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(java.util.stream.Collectors.toList());
                commentsList.addAll(list);
            }
        }
        dtoBuilder.comments(commentsList);
        dtoBuilder.information(product.getInformation());
        dtoBuilder.preparationTime(product.getPreparationTime());
        if (product.getCategoryId() != null) {
            try {
                categoryRepository.findById(product.getCategoryId()).ifPresent(cat -> {
                    CategoryResponseDTO catDto = CategoryResponseDTO.builder()
                            .categoryId(cat.getCategoryId())
                            .name(cat.getName())
                            .parameterId(cat.getExternalId())
                            .parameterName(null)
                            .status(cat.getStatus())
                            .companyId(cat.getCompanyId())
                            .createdAt(cat.getCreatedAt())
                            .updatedAt(cat.getUpdatedAt())
                            .build();
                    dtoBuilder.category(catDto);
                });
            } catch (Exception ignored) {}
        }
        return dtoBuilder.build();
    }

    private Map<Long, ProductDiscount> resolveActiveDiscounts(List<Product> products, Long companyId) {
        List<Long> productIds = products.stream()
                .map(Product::getProductId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return Map.of();
        }

        Long resolvedCompanyId = companyId != null
                ? companyId
                : products.stream().map(Product::getCompanyId).filter(java.util.Objects::nonNull).findFirst().orElse(null);

        return productDiscountSupport.findActiveDiscountsByProductIds(resolvedCompanyId, productIds);
    }
}
