package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.CityUseCase;
import com.restaurante.bot.model.City;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "City", description = "APIs para la gestión de ciudades")
@RestController
@RequestMapping("/${app.request.mapping}/city")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RequiredArgsConstructor
@Slf4j
public class CityController {

    private final CityUseCase cityUseCase;

    @PostMapping("/create")
    public ResponseEntity<City> save(@RequestBody @Valid City city) {
        return ResponseEntity.ok(cityUseCase.save(city));
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(cityUseCase.get(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<City> update(@PathVariable("id") Long cityId,
            @RequestBody @Valid City city) {
        return ResponseEntity.ok(cityUseCase.update(cityId, city));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        boolean result = cityUseCase.delete(id);
        if (result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<City>> getAll(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(cityUseCase.getAll(customQuery));
    }

    @GetMapping
    public ResponseEntity<Page<City>> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = com.restaurante.bot.util.SortConstants.ASC) String orders,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(cityUseCase.getAll(page, size, orders, sortBy));
    }

    @GetMapping("/get-all-without-page")
    public ResponseEntity<List<City>> getAllWithoutPage(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(cityUseCase.getAllWithOutPage(customQuery));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<City>> search(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(cityUseCase.searchCustom(customQuery));
    }

}
