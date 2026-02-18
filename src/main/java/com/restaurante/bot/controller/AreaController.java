package com.restaurante.bot.controller;

import com.restaurante.bot.dto.AreaDto;
import com.restaurante.bot.dto.AreaGetAllDto;
import com.restaurante.bot.dto.AreaSaveAndUpdateDto;

import com.restaurante.bot.business.interfaces.IAreaService;
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

@Tag(name = "Area", description = "APIs para la gestión de áreas")
@RestController
@RequestMapping("/${app.request.mapping}/area")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class AreaController {

    private final IAreaService iAreaService;

    @PostMapping("/create")
    public ResponseEntity<AreaDto> save(@RequestBody @Valid AreaSaveAndUpdateDto areaDto) {
        return ResponseEntity.ok(iAreaService.save(areaDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaDto> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(iAreaService.get(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AreaDto> update(@PathVariable("id") Long areaId, @RequestBody @Valid AreaSaveAndUpdateDto areaDto) {
        return ResponseEntity.ok(iAreaService.update(areaId, areaDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        boolean result = iAreaService.delete(id);
        if (result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<AreaGetAllDto>> getAll(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(iAreaService.getAll(customQuery));
    }

    @GetMapping
    public ResponseEntity<Page<AreaGetAllDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "5") int size,
                                                      @RequestParam(defaultValue = "ASC") String orders,
                                                      @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(iAreaService.getAll(page, size, orders, sortBy));
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<AreaGetAllDto>> getAllWithoutPage(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(iAreaService.getAllWithOutPage(customQuery));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AreaGetAllDto>> search(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(iAreaService.searchCustom(customQuery));
    }
}
