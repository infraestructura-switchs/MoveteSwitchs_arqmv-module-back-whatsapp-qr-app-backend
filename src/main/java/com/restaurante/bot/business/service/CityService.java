package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.CityInterface;
import com.restaurante.bot.model.City;
import com.restaurante.bot.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CityService implements CityInterface {

    private final CityRepository cityRepository;

    @Override
    public List<City> getCities() {
        return cityRepository.findAll();
    }
}
