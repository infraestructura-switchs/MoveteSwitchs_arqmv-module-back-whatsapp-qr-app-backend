package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.CityUseCase;
import com.restaurante.bot.business.interfaces.CityInterface;
import com.restaurante.bot.model.City;
import com.restaurante.bot.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import com.restaurante.bot.exception.GenericException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService implements CityInterface, CityUseCase {

    private final CityRepository cityRepository;

    @Override
    public List<City> getCities() {
        return cityRepository.findAll();
    }

    @Override
    public City save(City city) {
        return cityRepository.save(city);
    }

    @Override
    public City get(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new GenericException("ciudad no encontrada", HttpStatus.BAD_REQUEST));
    }

    @Override
    public City update(Long id, City city) {
        City existing = cityRepository.findById(id)
                .orElseThrow(() -> new GenericException("ciudad no encontrada", HttpStatus.BAD_REQUEST));

        if (city.getName() != null) existing.setName(city.getName());
        if (city.getStatus() != null) existing.setStatus(city.getStatus());

        return cityRepository.save(existing);
    }

    @Override
    public boolean delete(Long id) {
        if (cityRepository.existsById(id)) {
            cityRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Page<City> getAll(Map<String, String> customQuery) {
        int page = 0;
        int size = 5;
        try {
            if (customQuery.containsKey("page")) page = Integer.parseInt(customQuery.get("page"));
            if (customQuery.containsKey("size")) size = Integer.parseInt(customQuery.get("size"));
        } catch (NumberFormatException e) {
            // ignore and use defaults
        }
        return listarPaged(page, size);
    }

    @Override
    public Page<City> getAll(int page, int size, String orders, String sortBy) {
        return listarPaged(page, size);
    }

    @Override
    public List<City> getAllWithOutPage(Map<String, String> customQuery) {
        return getCities();
    }

    @Override
    public Page<City> searchCustom(Map<String, String> customQuery) {
        return listarPaged(0, 5);
    }

    // helper for paging
    public Page<City> listarPaged(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        return cityRepository.findAll(pageable);
    }
}
