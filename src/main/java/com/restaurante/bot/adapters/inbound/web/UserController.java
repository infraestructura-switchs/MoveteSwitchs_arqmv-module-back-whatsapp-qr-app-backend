package com.restaurante.bot.adapters.inbound.web;

import com.restaurante.bot.application.ports.incoming.UserUseCase;
import com.restaurante.bot.dto.GgpUserGetAllDto;
import com.restaurante.bot.dto.GgpUserSaveAndUpdateDto;
import com.restaurante.bot.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${app.request.mapping}/user")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody /*...*/ Object loginIn) {
        // redirect to login port (if defined) or another use case
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> save(@RequestBody GgpUserSaveAndUpdateDto user) {
        return ResponseEntity.ok(userUseCase.save(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(userUseCase.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<GgpUserGetAllDto>> getAll(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(userUseCase.getAll(customQuery));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<GgpUserGetAllDto>> getAllWithOutPage(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(userUseCase.getAllWithoutPage(customQuery));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GgpUserGetAllDto>> search(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(userUseCase.search(customQuery));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable("userId") Long userId,
            @RequestBody GgpUserSaveAndUpdateDto user) {
        return ResponseEntity.ok(userUseCase.update(userId, user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        boolean result = userUseCase.delete(id);
        if (result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
