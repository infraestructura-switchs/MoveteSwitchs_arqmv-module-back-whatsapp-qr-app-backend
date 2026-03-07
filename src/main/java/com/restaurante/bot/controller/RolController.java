package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.RolUseCase;
import com.restaurante.bot.dto.RolDto;
import com.restaurante.bot.dto.RolGetAllDto;
import com.restaurante.bot.dto.RolSaveAndUpdateDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/${app.request.mapping}/rol")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RolController {

    private final RolUseCase rolUseCase;

    @PostMapping("/create")
    public ResponseEntity<RolDto> save(@RequestBody @Valid RolSaveAndUpdateDto rolDto) {
        return ResponseEntity.ok(rolUseCase.save(rolDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDto> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(rolUseCase.get(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RolDto> update(@PathVariable("id") Long rolId,
            @RequestBody @Valid RolSaveAndUpdateDto rolDto) {
        return ResponseEntity.ok(rolUseCase.update(rolId, rolDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        boolean result = rolUseCase.delete(id);
        if (result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all-other")
    public ResponseEntity<Page<RolGetAllDto>> getAll(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(rolUseCase.getAll(customQuery));
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<RolGetAllDto>> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ASC") String orders,
            @RequestParam(defaultValue = "rolId") String sortBy) {
        return ResponseEntity.ok(rolUseCase.getAll(page, size, orders, sortBy));
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<RolGetAllDto>> getAllWithoutPage() {
        return ResponseEntity.ok(rolUseCase.getAllWithOutPage());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RolGetAllDto>> search(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(rolUseCase.searchCustom(customQuery));
    }
}
