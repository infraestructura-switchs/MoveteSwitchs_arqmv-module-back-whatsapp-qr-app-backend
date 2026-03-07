package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.ParameterUseCase;
import com.restaurante.bot.dto.ParameterRequestDTO;
import com.restaurante.bot.dto.ParameterResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/parameters")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ParameterController {

    private final ParameterUseCase parameterUseCase;

    @GetMapping
    public ResponseEntity<List<ParameterResponseDTO>> getAllParameters() {
        return ResponseEntity.ok(parameterUseCase.getAllParameters());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ParameterResponseDTO>> getParametersByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.ok(parameterUseCase.getParametersByCompanyId(companyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParameterResponseDTO> getParameterById(@PathVariable Long id) {
        return ResponseEntity.ok(parameterUseCase.getParameterById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ParameterResponseDTO> getParameterByName(@PathVariable String name) {
        return ResponseEntity.ok(parameterUseCase.getParameterByName(name));
    }

    @GetMapping("/company/{companyId}/name/{name}")
    public ResponseEntity<ParameterResponseDTO> getParameterByNameAndCompanyId(
            @PathVariable String name, 
            @PathVariable Long companyId) {
        return ResponseEntity.ok(parameterUseCase.getParameterByNameAndCompanyId(name, companyId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ParameterResponseDTO>> getParametersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(parameterUseCase.getParametersByStatus(status));
    }

    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<List<ParameterResponseDTO>> getParametersByCompanyIdAndStatus(
            @PathVariable Long companyId, 
            @PathVariable String status) {
        return ResponseEntity.ok(parameterUseCase.getParametersByCompanyIdAndStatus(companyId, status));
    }

    @PostMapping
    public ResponseEntity<ParameterResponseDTO> createParameter(@Valid @RequestBody ParameterRequestDTO request) {
        return new ResponseEntity<>(parameterUseCase.createParameter(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParameterResponseDTO> updateParameter(
            @PathVariable Long id,
            @Valid @RequestBody ParameterRequestDTO request) {
        return ResponseEntity.ok(parameterUseCase.updateParameter(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParameter(@PathVariable Long id) {
        parameterUseCase.deleteParameter(id);
        return ResponseEntity.noContent().build();
    }
}