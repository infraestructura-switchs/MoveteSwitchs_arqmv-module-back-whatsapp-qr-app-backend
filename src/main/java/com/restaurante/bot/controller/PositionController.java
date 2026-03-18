package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.PositionUseCase;
import com.restaurante.bot.dto.PositionDto;
import com.restaurante.bot.dto.PositionGetAllDto;
import com.restaurante.bot.dto.PositionSaveAndUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${app.request.mapping}/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionUseCase positionUseCase;

    @PostMapping("/create")
    public ResponseEntity<PositionDto> create(@RequestBody PositionSaveAndUpdateDto dto) {
        return ResponseEntity.ok(positionUseCase.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(positionUseCase.get(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PositionDto> update(@PathVariable Long id, @RequestBody PositionSaveAndUpdateDto dto) {
        return ResponseEntity.ok(positionUseCase.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(positionUseCase.delete(id));
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<PositionGetAllDto>> getAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(positionUseCase.getAll(params));
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<PositionGetAllDto>> getAllWithoutPage(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(positionUseCase.getAllWithOutPage(params));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PositionGetAllDto>> search(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(positionUseCase.searchCustom(params));
    }

    // Endpoint para filtrar posiciones por usuario
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<PositionGetAllDto>> getByUser(@PathVariable Long userId) {
        // Implementación pendiente: deberás agregar la lógica en el servicio para filtrar por usuario
        // return ResponseEntity.ok(positionService.getByUser(userId));
        return ResponseEntity.ok(List.of()); // Placeholder
    }
}
