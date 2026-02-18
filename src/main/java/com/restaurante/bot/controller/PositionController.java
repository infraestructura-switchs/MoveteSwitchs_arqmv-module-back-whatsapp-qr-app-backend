package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.IPositionService;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PositionController {

    private final IPositionService positionService;

    @PostMapping("/create")
    public ResponseEntity<PositionDto> create(@RequestBody PositionSaveAndUpdateDto dto) {
        return ResponseEntity.ok(positionService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(positionService.get(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PositionDto> update(@PathVariable Long id, @RequestBody PositionSaveAndUpdateDto dto) {
        return ResponseEntity.ok(positionService.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(positionService.delete(id));
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<PositionGetAllDto>> getAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(positionService.getAll(params));
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<PositionGetAllDto>> getAllWithoutPage(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(positionService.getAllWithOutPage(params));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PositionGetAllDto>> search(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(positionService.searchCustom(params));
    }

    // Endpoint para filtrar posiciones por usuario
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<PositionGetAllDto>> getByUser(@PathVariable Long userId) {
        // Implementación pendiente: deberás agregar la lógica en el servicio para filtrar por usuario
        // return ResponseEntity.ok(positionService.getByUser(userId));
        return ResponseEntity.ok(List.of()); // Placeholder
    }
}
