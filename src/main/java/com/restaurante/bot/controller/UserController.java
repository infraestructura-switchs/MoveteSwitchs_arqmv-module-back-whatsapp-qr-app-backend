package com.restaurante.bot.controller;


import com.mashape.unirest.http.exceptions.UnirestException;
import com.restaurante.bot.business.interfaces.LoginService;
import com.restaurante.bot.business.interfaces.UserService;
import com.restaurante.bot.dto.*;
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
@RequestMapping("/${app.request.mapping}/user")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {

    private final UserService iUserService;
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginOut> login(@Valid @RequestBody LoginIn loginIn) {
        return ResponseEntity.ok(loginService.login(loginIn));
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> save(@RequestBody GgpUserSaveAndUpdateDto user) {
        return ResponseEntity.ok(iUserService.save(user));
    }

    @PostMapping("/forget")
    public ResponseEntity<ForgotPasswordUserDto> forgotPassword(@RequestBody GgpForgotPasswordDto ggpForgotPasswordDto) throws UnirestException {
        return ResponseEntity.ok(iUserService.forgotPassword(ggpForgotPasswordDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(iUserService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<GgpUserGetAllDto>> getAll(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(iUserService.getAll(customQuery));
    }

    @GetMapping("/v1/")
    public ResponseEntity<Page<GgpUserGetAllDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size,
                                                         @RequestParam(defaultValue = "ASC") String orders,
                                                         @RequestParam(defaultValue = "id" ) String sortBy) {
        return ResponseEntity.ok(iUserService.getAll(page , size, orders ,sortBy));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<GgpUserGetAllDto>> getAllWithOutPage(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(iUserService.getAllWithOutPage(customQuery));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GgpUserGetAllDto>> search(@RequestParam Map<String, String> customQuery) {
        return ResponseEntity.ok(iUserService.searchCustom(customQuery));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable("userId")Long userId, @RequestBody GgpUserSaveAndUpdateDto user) {
        return ResponseEntity.ok(iUserService.update(userId, user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        boolean result = iUserService.delete(id);
        if (result) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
