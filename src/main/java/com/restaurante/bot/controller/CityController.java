package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.CityInterface;
import com.restaurante.bot.model.City;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/city")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class CityController {

    private  final CityInterface cityInterface;

    @GetMapping("/getAllNoPage")
    public ResponseEntity<List<City>>  getAllNoPage() {
        log.info("getAllNoPage");
        return new ResponseEntity<>(cityInterface.getCities(), HttpStatus.OK);
    }
}
