package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.CategoryUseCase;
import com.restaurante.bot.dto.CategoryRequestDTO;
import com.restaurante.bot.dto.CategoryResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${app.request.mapping}/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CategoryController {

    private final CategoryUseCase categoryUseCase;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryUseCase.getAllCategories());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.ok(categoryUseCase.getCategoriesByCompanyId(companyId));
    }

    @GetMapping("/external-company/{externalCompanyId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByExternalCompanyId(@PathVariable Long externalCompanyId) {
        return ResponseEntity.ok(categoryUseCase.getCategoriesByExternalCompanyId(externalCompanyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryUseCase.getCategoryById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryUseCase.getCategoryByName(name));
    }

    @GetMapping("/company/{companyId}/name/{name}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByNameAndCompanyId(
            @PathVariable String name, 
            @PathVariable Long companyId) {
        return ResponseEntity.ok(categoryUseCase.getCategoryByNameAndCompanyId(name, companyId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(categoryUseCase.getCategoriesByStatus(status));
    }

    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByCompanyIdAndStatus(
            @PathVariable Long companyId, 
            @PathVariable String status) {
        return ResponseEntity.ok(categoryUseCase.getCategoriesByCompanyIdAndStatus(companyId, status));
    }

    @GetMapping("/parameter/{parameterId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByParameterId(@PathVariable Long parameterId) {
        return ResponseEntity.ok(categoryUseCase.getCategoriesByParameterId(parameterId));
    }

    @GetMapping("/company/{companyId}/parameter/{parameterId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByCompanyIdAndParameterId(
            @PathVariable Long companyId, 
            @PathVariable Long parameterId) {
        return ResponseEntity.ok(categoryUseCase.getCategoriesByCompanyIdAndParameterId(companyId, parameterId));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        return new ResponseEntity<>(categoryUseCase.createCategory(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(categoryUseCase.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryUseCase.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<CategoryResponseDTO>> getAll(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(categoryUseCase.getAll(customQuery));
    }

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<Page<CategoryResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ASC") String orders,
            @RequestParam(defaultValue = "categoryId") String sortBy) {
        return ResponseEntity.ok(categoryUseCase.getAll(page, size, orders, sortBy));
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<CategoryResponseDTO>> getAllWithoutPage(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(categoryUseCase.getAllWithOutPage(customQuery));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CategoryResponseDTO>> search(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(categoryUseCase.searchCustom(customQuery));
    }
}