package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.CategoryService;
import com.restaurante.bot.dto.CategoryRequestDTO;
import com.restaurante.bot.dto.CategoryResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.ok(categoryService.getCategoriesByCompanyId(companyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    @GetMapping("/company/{companyId}/name/{name}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByNameAndCompanyId(
            @PathVariable String name, 
            @PathVariable Long companyId) {
        return ResponseEntity.ok(categoryService.getCategoryByNameAndCompanyId(name, companyId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(categoryService.getCategoriesByStatus(status));
    }

    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByCompanyIdAndStatus(
            @PathVariable Long companyId, 
            @PathVariable String status) {
        return ResponseEntity.ok(categoryService.getCategoriesByCompanyIdAndStatus(companyId, status));
    }

    @GetMapping("/parameter/{parameterId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByParameterId(@PathVariable Long parameterId) {
        return ResponseEntity.ok(categoryService.getCategoriesByParameterId(parameterId));
    }

    @GetMapping("/company/{companyId}/parameter/{parameterId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByCompanyIdAndParameterId(
            @PathVariable Long companyId, 
            @PathVariable Long parameterId) {
        return ResponseEntity.ok(categoryService.getCategoriesByCompanyIdAndParameterId(companyId, parameterId));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        return new ResponseEntity<>(categoryService.createCategory(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}