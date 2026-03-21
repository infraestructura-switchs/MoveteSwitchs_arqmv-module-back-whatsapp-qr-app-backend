package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.model.City;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CityInterface {

    List<City> getCities();

    City save(City city);
    City get(Long id);
    City update(Long id, City city);
    boolean delete(Long id);

    Page<City> getAll(Map<String, String> customQuery);
    Page<City> getAll(int page, int size, String orders, String sortBy);
    List<City> getAllWithOutPage(Map<String, String> customQuery);
    Page<City> searchCustom(Map<String, String> customQuery);
}
