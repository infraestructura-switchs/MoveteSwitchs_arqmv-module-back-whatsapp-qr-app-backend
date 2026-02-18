package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.RolDto;
import com.restaurante.bot.dto.RolGetAllDto;
import com.restaurante.bot.dto.RolSaveAndUpdateDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface RolInterface {

    // Método para obtener un Rol por su ID
    RolDto get(long id);

    // Método para guardar un nuevo Rol
    RolDto save(RolSaveAndUpdateDto rolDto);

    // Método para actualizar un Rol existente
    RolDto update(Long rolId, RolSaveAndUpdateDto rolDto);

    // Método para eliminar (baja lógica) un Rol
    boolean delete(Long id);

    // Método para obtener todos los roles con filtro personalizado
    Page<RolGetAllDto> getAll(Map<String, String> customQuery);

    // Método para obtener todos los roles con paginación
    Page<RolGetAllDto> getAll(int page, int size, String orders, String sortBy);

    // Método para obtener todos los roles sin paginación
    List<RolGetAllDto> getAllWithOutPage();

    // Método para realizar una búsqueda personalizada de roles
    Page<RolGetAllDto> searchCustom(Map<String, String> customQuery);
}
