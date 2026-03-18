package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.LandingTemplateUseCase;
import com.restaurante.bot.dto.LandingTemplateRequest;
import com.restaurante.bot.dto.LandingTemplateResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${app.request.mapping}/landing-template")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@Slf4j
public class LandingTemplateController {

    private final LandingTemplateUseCase landingTemplateUseCase;

    @PostMapping("/create")
    public ResponseEntity<LandingTemplateRequest> createLandingTemplate(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "status", required = false) String status) {

        LandingTemplateRequest request = LandingTemplateRequest.builder()
                .name(name)
                .status(status)
                .build();

        LandingTemplateRequest saved = landingTemplateUseCase.save(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<LandingTemplateResponseDTO>> getAll(@RequestParam Map<String, String> customQuery) {
        return new ResponseEntity<>(landingTemplateUseCase.getAll(customQuery), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<LandingTemplateResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String orders,
            @RequestParam(defaultValue = "landingTemplateId") String sortBy) {
        return new ResponseEntity<>(landingTemplateUseCase.getAllPageLandingTemplate(page, size, orders, sortBy), HttpStatus.OK);
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<LandingTemplateResponseDTO>> getAllWithoutPage(@RequestParam Map<String, String> customQuery) {
        return new ResponseEntity<>(landingTemplateUseCase.getAllWithoutPage(customQuery), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LandingTemplateRequest> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(landingTemplateUseCase.get(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        landingTemplateUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateById/{id}")
    public ResponseEntity<LandingTemplateRequest> updateLandingTemplate(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) String status) {

        LandingTemplateRequest request = LandingTemplateRequest.builder()
                .landingTemplateId(id)
                .name(name)
                .status(status)
                .build();

        LandingTemplateRequest updated = landingTemplateUseCase.update(request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LandingTemplateResponseDTO>> search(@RequestParam Map<String, String> customQuery) {
        return new ResponseEntity<>(landingTemplateUseCase.searchCustom(customQuery), HttpStatus.OK);
    }
}
