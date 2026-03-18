package com.restaurante.bot.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.api.dto.GroupDTO;
import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.application.ports.incoming.ProductUseCase;
import com.restaurante.bot.business.interfaces.ProductInterface;
import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductCategoryDTO;
import com.restaurante.bot.dto.ProductDto;
import com.restaurante.bot.dto.ProductUpdateDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService implements ProductInterface, ProductUseCase {

    private final ProductRepository productRepository;
    private final CallServiceHttp callServiceHttp;
    private final CategoryRepository categoryRepository;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;
    private final CategoryMappingRepository categoryMappingRepository;
    private final ParameterRepository parameterRepository;
    private final ProductDiscountSupport productDiscountSupport;


    private final Map<Long, Map<String, Long>> dynamicCategoryMapping = new HashMap<>();

    private final String productImageDefault ="PRODUCT_IMAGE_DEFAULT";

    // Backwards-compatible field used by tests via ReflectionTestUtils
    private String defaultProductImage = productImageDefault;

    private final String productImageChuzoIvan ="PRODUCT_IMAGE_CHUZO_IVAN";

    private final String productImageBuenNino ="PRODUCT_IMAGE_BUEN_NINO";

    private final Map<Integer, String> productImageParameterIDs = Map.of(238,productImageChuzoIvan,
            273,productImageBuenNino);

    private String imageOrDefault(String imgProduct, String productImageBycompany) {

        String productImageDefault = imgProduct;
        if (imgProduct == null) productImageDefault = productImageBycompany;

        String imgProductTrim = productImageDefault.trim();
        if (imgProductTrim.isEmpty() || "null".equalsIgnoreCase(imgProductTrim)) productImageDefault =  imgProductTrim;

        return productImageDefault;
    }

    private String imageByCompanyId(Integer companyId) {
        log.info("Obteniendo imagen para producto de la compañía {}", companyId);
        String productIdImage = productImageParameterIDs.getOrDefault(companyId, productImageDefault);


        Optional<Parameter> parameteOptiona = parameterRepository.findByName(productIdImage);
        return parameteOptiona.map(Parameter::getValue).orElseGet(() -> {
            log.warn("No se encontró el parámetro {} para la imagen del producto, se usará un valor por defecto", productImageDefault);
            return "https://res.cloudinary.com/dzj8q4qeu/image/upload/v1701304417/restaurante-bot/product-default.png";
        });
    }

    private ProductDto toDto(Product product, String productImageBycompany, ProductDiscount activeDiscount) {
        List<String> commentsList = new ArrayList<>();
        if (product.getProductComments() != null && !product.getProductComments().isEmpty()) {
            commentsList = product.getProductComments().stream()
                .map(pc -> pc.getComment() != null ? pc.getComment().getText() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } else if (product.getComments() != null && !product.getComments().trim().isEmpty()) {
            try {
                commentsList = objectMapper.readValue(product.getComments(), new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                // fallback: try CSV
                commentsList = Arrays.stream(product.getComments().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        }
        ProductDiscountSupport.ProductPriceSummary priceSummary = productDiscountSupport.summarize(product.getPrice(), activeDiscount);
        return ProductDto.builder()
            .id(product.getProductId())
            .productName(product.getName())
            .price(priceSummary.finalPrice())
            .originalPrice(priceSummary.originalPrice())
            .discountAmount(priceSummary.discountAmount())
            .description(product.getDescription())
            .status(product.getStatus())
            .image(imageOrDefault(product.getImgProduct(), productImageBycompany))
            .categoryId(product.getCategoryId())
            .category(null)
            .companyId(product.getCompanyId())
            .comments(commentsList)
            .information(product.getInformation())
            .preparationTime(product.getPreparationTime())
            .activeDiscount(productDiscountSupport.toDto(activeDiscount))
            .build();
    }

    @Override
    public CategorizedProductsDTO getProductsSfotRestaurantByCompanyId(Long companyId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new GenericException("No autenticado", HttpStatus.UNAUTHORIZED);
        }

        if (!companyRepository.existsByExternalCompanyId(companyId)) { // Asume companyRepository inyectado
            throw  new GenericException("La compañia no existe", HttpStatus.NOT_FOUND);
        }

        List<Category> categories = categoryRepository.findByCompanyId(companyId);
        List<Product> products = productRepository.findByCompanyIdOrderByNameAsc(companyId);

        String imageByCompany = imageByCompanyId(companyId.intValue());
        Map<Long, ProductDiscount> activeDiscounts = productDiscountSupport.findActiveDiscountsByProductIds(
            companyId,
            products.stream().map(Product::getProductId).collect(Collectors.toList()));

        Map<Long, List<ProductDto>> categorizedProductMap = products.stream()
            .map(product -> toDto(product, imageByCompany, activeDiscounts.get(product.getProductId())))
                .collect(Collectors.groupingBy(ProductDto::getCategoryId));

        CategorizedProductsDTO categorizedProductsDTO = new CategorizedProductsDTO();

        for (Category category : categories) {
            List<ProductDto> categoryProducts = categorizedProductMap.get(category.getCategoryId());
            if (categoryProducts != null && !categoryProducts.isEmpty()) {
                ProductCategoryDTO categoryDTO = new ProductCategoryDTO();
                categoryDTO.setCategoryName(category.getName());
                categoryDTO.setProducts(categoryProducts);
                categorizedProductsDTO.getCategories().add(categoryDTO);
            }
        }

        return categorizedProductsDTO;
    }

    @Transactional
    @Scheduled(cron = "0 00 07 * * ?")
    public void updateOrCreateProductsWithCategory() {
        log.info("Iniciando actualización programada de productos para todas las compañías");

        List<Long> companyIdsToProcess = companyRepository.findCompanyIds();
        for (Long companyId : companyIdsToProcess) {
            loadCategoryMapping(companyId);

            List<ProductDTO> products = callServiceHttp.getProduct(companyId);
            if (products == null || products.isEmpty()) {
                log.warn("No se encontraron productos para la compañía {}, se omite.", companyId);
                continue;
            }

            for (ProductDTO productDTO : products) {
                Product product = mapToProduct(productDTO, companyId);
                Long categoryId = getOrCreateCategory(productDTO, companyId);
                product.setCategoryId(categoryId);
                productRepository.save(product);
            }
        }

        log.info("Actualización de productos completada para todas las compañías");
    }

    @Transactional
    public GenericResponse updateOrCreateProductsWithCategory(Long companyId) {
        // Cargar o recargar el mapeo para esta compañía
        loadCategoryMapping(companyId);

        List<ProductDTO> products = callServiceHttp.getProduct(companyId);
        if (products == null || products.isEmpty()) {
            log.warn("No se encontraron productos para la compañía {}", companyId);
            return new GenericResponse("No se encontraron productos para actualizar", 200L);
        }

        for (ProductDTO productDTO : products) {
            Product product = mapToProduct(productDTO, companyId);

            Long categoryId = getOrCreateCategory(productDTO, companyId);
            product.setCategoryId(categoryId);

            productRepository.save(product);
        }

        return new GenericResponse("Productos actualizados con éxito", 200L);
    }


    private void loadCategoryMapping(Long companyId) {
        dynamicCategoryMapping.computeIfAbsent(companyId, k -> {
            Map<String, Long> mapping = new HashMap<>();
            List<CategoryMapping> mappings = categoryMappingRepository.findByCompanyIdAndStatus(companyId, "ACTIVE");
            for (CategoryMapping cm : mappings) {
                mapping.put(cm.getGroupId().toString(), cm.getCategoryId());
            }
            return mapping;
        });
    }

    private Long getOrCreateCategory(ProductDTO productDTO, Long companyId) {
        String groupId = productDTO.getData().getGrupo().getIdGrupo();
        String groupDescription = productDTO.getData().getGrupo().getDescripcion().toUpperCase();

        Map<String, Long> companyMapping = dynamicCategoryMapping.get(companyId);
        Long categoryId = companyMapping.get(groupId);  // Primero intenta con IdGrupo

        if (categoryId == null) {
            // Si no hay mapeo por IdGrupo, intenta con la descripción
            categoryId = companyMapping.get(groupDescription);
        }

        if (categoryId == null) {
            // Si no existe, crea una nueva categoría y actualiza el mapeo
            Category category = new Category();
            category.setName(groupDescription);
            category.setExternalId(Long.parseLong(groupId));
            category.setStatus("ACTIVE");
            category.setCompanyId(companyId);
            category = categoryRepository.save(category);
            categoryId = category.getCategoryId();

            // Actualizar el mapeo en memoria
            companyMapping.put(groupId, categoryId);
            companyMapping.put(groupDescription, categoryId);

            // Guardar en la base de datos el nuevo mapeo
            CategoryMapping mapping = new CategoryMapping();
            mapping.setGroupId(Long.parseLong(groupId));
            mapping.setCategoryId(categoryId);
            mapping.setCompanyId(companyId);
            mapping.setStatus("ACTIVE");
            categoryMappingRepository.save(mapping);
            log.info("Nueva categoría creada para groupId {}: {}", groupId, categoryId);
        }

        return categoryId;
    }

    @Override
    public ProductDto updateProductDescription(ProductUpdateDTO productUpdateDTO) {
        Product product = productRepository.findById(productUpdateDTO.getProductId())
                .orElseThrow(() -> new GenericException("Producto no encontrado con el id: " + productUpdateDTO.getProductId(), HttpStatus.BAD_REQUEST));

        List<String> commentsList = new ArrayList<>();
        if (product.getProductComments() != null && !product.getProductComments().isEmpty()) {
            commentsList = product.getProductComments().stream()
                    .map(pc -> pc.getComment() != null ? pc.getComment().getText() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else if (product.getComments() != null && !product.getComments().trim().isEmpty()) {
            try {
                commentsList = objectMapper.readValue(product.getComments(), new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                commentsList = Arrays.stream(product.getComments().split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            }
        }
        product.setDescription(productUpdateDTO.getDescription());

        Product updatedProduct = productRepository.save(product);

        String imageByCompany = imageByCompanyId(product.getCompanyId().intValue());
        ProductDiscount activeDiscount = productDiscountSupport.findActiveDiscount(product.getCompanyId(), product.getProductId());
        ProductDiscountSupport.ProductPriceSummary priceSummary = productDiscountSupport.summarize(updatedProduct.getPrice(), activeDiscount);

        return ProductDto.builder()
            .id(updatedProduct.getProductId())
            .productName(updatedProduct.getName())
            .price(priceSummary.finalPrice())
            .originalPrice(priceSummary.originalPrice())
            .discountAmount(priceSummary.discountAmount())
            .description(updatedProduct.getDescription())
            .status(updatedProduct.getStatus())
            .image(imageOrDefault(updatedProduct.getImgProduct(), imageByCompany))
            .categoryId(updatedProduct.getCategoryId())
            .category(null)
            .companyId(updatedProduct.getCompanyId())
            .comments(commentsList)
            .information(updatedProduct.getInformation())
            .preparationTime(updatedProduct.getPreparationTime())
            .activeDiscount(productDiscountSupport.toDto(activeDiscount))
            .build();
    }

    private Product mapToProduct(ProductDTO productDTO, Long companyId) {
        Product product = new Product();

        product.setArqProductId(productDTO.getId().intValue());
        product.setSoftRestaurantId(productDTO.getIdProducto());
        product.setName(productDTO.getData().getDescripcion());
        product.setPrice(productDTO.getData().getPrecio());
        product.setStatus("ACTIVE");
        product.setImgProduct(productDTO.getData().getImagenMenu());
        product.setCompanyId(companyId);
        product.setGroupId(Long.parseLong(productDTO.getData().getGrupo().getIdGrupo()));

        try {
                if (productDTO.getData().getComentarios() != null) {
                product.setComments(objectMapper.writeValueAsString(productDTO.getData().getComentarios()));
                // do not create normalized ProductComment rows here because existing table schema differs
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializando comentarios para productId {}: {}", productDTO.getId(), e.getMessage());
            product.setComments("[]");
        }

        product.setInformation(productDTO.getData().getInformacion());
        product.setPreparationTime(productDTO.getData().getMinutosPreparacion() != null ?
                Math.toIntExact(Math.round(productDTO.getData().getMinutosPreparacion())) : 0);

        Optional<Product> existingProduct = productRepository.findByArqProductIdAndCompanyId(product.getArqProductId(), companyId);

        if (existingProduct.isPresent()) {
            Product existing = existingProduct.get();
            existing.setName(product.getName());
            existing.setSoftRestaurantId(product.getSoftRestaurantId());
            existing.setPrice(product.getPrice());
            existing.setStatus(product.getStatus());
            existing.setImgProduct(product.getImgProduct());
            existing.setCompanyId(product.getCompanyId());
            existing.setGroupId(product.getGroupId());
            existing.setComments(product.getComments());
            existing.setInformation(product.getInformation());
            existing.setPreparationTime(product.getPreparationTime());
            return existing;
        } else {
            return product;
        }
    }

    @Override
    public List<ProductDto> searchProducts(Long companyId, String name, String categoryName) {
        String term = (name == null || name.isBlank()) ? null : name.trim();

        String imageByCompany = imageByCompanyId(companyId.intValue());

        Long categoryId = null;
        if (categoryName != null && !categoryName.isBlank()) {
            categoryId = categoryRepository
                    .findByCompanyIdAndNameIgnoreCase(companyId, categoryName.trim())
                    .map(Category::getCategoryId)
                    .orElse(null);
            if (categoryId == null) {
                return List.of();
            }
        }

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 1000);
        org.springframework.data.domain.Page<com.restaurante.bot.model.Product> foundPage = productRepository.search(companyId, term, categoryId, pageable);
        Map<Long, ProductDiscount> activeDiscounts = productDiscountSupport.findActiveDiscountsByProductIds(
                companyId,
                foundPage.getContent().stream().map(Product::getProductId).collect(Collectors.toList()));

        return foundPage.getContent().stream()
            .map(product -> toDto(product, imageByCompany, activeDiscounts.get(product.getProductId())))
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> listByPrice(Long companyId, String categoryName, String sort, String name) {
        String order = null;
        if (sort != null) {
            String s = sort.trim().toUpperCase();
            if ("ASC".equals(s) || "DESC".equals(s)) order = s;
        }
        if (order == null) order = "ASC";

        Long categoryId = null;
        if (categoryName != null && !categoryName.isBlank()) {
            categoryId = categoryRepository
                    .findByCompanyIdAndNameIgnoreCase(companyId, categoryName.trim())
                    .map(Category::getCategoryId)
                    .orElse(null);
            if (categoryId == null) return List.of();
        }

        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 1000,
            Sort.by(Sort.Direction.fromString(order), "price"));
        org.springframework.data.domain.Page<Product> foundPage = productRepository
            .findAllByCompanyAndCategoryAndNameOrderByPrice(companyId, categoryId, name, pageable);

        String imageByCompany = imageByCompanyId(companyId.intValue());
        Map<Long, ProductDiscount> activeDiscounts = productDiscountSupport.findActiveDiscountsByProductIds(
                companyId,
                foundPage.getContent().stream().map(Product::getProductId).collect(Collectors.toList()));

        return foundPage.getContent().stream()
            .map(product -> toDto(product, imageByCompany, activeDiscounts.get(product.getProductId())))
            .toList();
    }

    private Category mapToCategory(GroupDTO groupDTO, Long companyId) {
        Category category = new Category();
        category.setName(groupDTO.getDescripcion());
        category.setExternalId(Long.parseLong(groupDTO.getIdGrupo()));
        category.setStatus("ACTIVO");
        category.setCompanyId(companyId);
        return category;
    }
}
