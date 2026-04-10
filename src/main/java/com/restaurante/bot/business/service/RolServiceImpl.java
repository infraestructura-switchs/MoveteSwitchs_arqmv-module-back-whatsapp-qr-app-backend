package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.RolInterface;
import com.restaurante.bot.dto.RolDto;
import com.restaurante.bot.dto.RolGetAllDto;
import com.restaurante.bot.dto.RolSaveAndUpdateDto;
import com.restaurante.bot.exception.CustomErrorException;
import com.restaurante.bot.model.Rol;
import com.restaurante.bot.repository.RolRepository;
import com.restaurante.bot.util.Constants;
import com.restaurante.bot.util.SearchDTOConverter;
import com.restaurante.bot.dto.RolSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.restaurante.bot.application.ports.incoming.RolUseCase;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class RolServiceImpl implements RolInterface, RolUseCase {

    private final RolRepository rolRepository;

    @Override
    @Transactional
    public RolDto save(RolSaveAndUpdateDto rolDto) {
        Optional<Rol> rolOptional = rolRepository.findByName(rolDto.getName());
        if (rolOptional.isPresent()) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "El rol ya existe");
        }

        Rol Rol = new Rol();
        BeanUtils.copyProperties(rolDto, Rol);
        Rol.setStatus(Constants.ACTIVE_STATUS);

        Rol newRol = rolRepository.save(Rol);
        return mapRolDto(newRol);
    }

    @Override
    @Transactional
    public RolDto update(Long rolId, RolSaveAndUpdateDto rolDto) {
        Optional<Rol> rolOptional = rolRepository.findById(rolId);
        if (!rolOptional.isPresent()) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "El rol no existe");
        }

        Rol Rol = rolOptional.get();
        BeanUtils.copyProperties(rolDto, Rol);

        Rol updatedRol = rolRepository.save(Rol);
        return mapRolDto(updatedRol);
    }

    @Override
    @Transactional
    public boolean delete(Long rolId) {
        Optional<Rol> rolOptional = rolRepository.findById(rolId);
        if (rolOptional.isPresent()) {
            Rol Rol = rolOptional.get();
            Rol.setStatus(Constants.INACTIVE_STATUS);
            rolRepository.save(Rol);
            return true;
        }
        return false;
    }

    @Override
    public RolDto get(long id) {
        Optional<Rol> rolOptional = rolRepository.findById(id);
        if (rolOptional.isPresent()) {
            return mapRolDto(rolOptional.get());
        } else {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "El rol no existe");
        }
    }

    @Override
    public Page<RolGetAllDto> getAll(Map<String, String> customQuery) {
        String orders = com.restaurante.bot.util.SortConstants.ASC;
        String sortBy = "rolId";
        int page = 0;
        int size = 5;
        String status = Constants.ACTIVE_STATUS;

        if (customQuery.containsKey("status")) {
            status = customQuery.get("status");
        }
        if (customQuery.containsKey("orders")) {
            orders = customQuery.get("orders");
        }
        if (customQuery.containsKey("sortBy")) {
            sortBy = customQuery.get("sortBy");
        }
        if (customQuery.containsKey("page")) {
            page = Integer.parseInt(customQuery.get("page"));
        }
        if (customQuery.containsKey("size")) {
            size = Integer.parseInt(customQuery.get("size"));
        }

        Sort.Direction direction = Sort.Direction.fromString(orders);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return mapPageRolDto(rolRepository.findByStatus(status, pagingSort), pagingSort);
    }

    @Override
    public Page<RolGetAllDto> getAll(int page, int size, String orders, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(orders);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return mapPageRolDto(rolRepository.findByStatus(Constants.ACTIVE_STATUS, pagingSort), pagingSort);
    }

    @Override
    public List<RolGetAllDto> getAllWithOutPage() {

        return rolRepository.findByStatus(Constants.ACTIVE_STATUS).stream()
                .map(rol -> RolGetAllDto.builder()
                        .id(rol.getRolId())
                        .name(rol.getName())
                        .status(rol.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<RolGetAllDto> searchCustom(Map<String, String> customQuery) {
        // Convert Map parameters to RolSearchDTO for type-safe access
        RolSearchDTO searchRequest = SearchDTOConverter.toRolSearch(customQuery);
        searchRequest.validate();

        Sort.Direction direction = Sort.Direction.fromString(searchRequest.getOrders());
        Pageable pageable = PageRequest.of(
            searchRequest.getPage(), 
            searchRequest.getSize(), 
            Sort.by(direction, searchRequest.getSortBy()));

        // Use the refactored repository method
        return mapPageRolDto(
            rolRepository.findByIdOrNameContainingIgnoreCaseAndStatus(
                searchRequest.getRolId(),
                searchRequest.getName(),
                pageable),
            pageable);
    }

    private RolDto mapRolDto(Rol Rol) {
        RolDto rolDto = new RolDto();
        BeanUtils.copyProperties(Rol, rolDto);
        return rolDto;
    }

    private Page<RolGetAllDto> mapPageRolDto(Page<Rol> entityPage, Pageable pagingSort) {
        int totalElements = (int) entityPage.getTotalElements();
        return new PageImpl<>(
                entityPage.getContent().stream()
                        .map(rol -> RolGetAllDto.builder()
                                .id(rol.getRolId())
                                .name(rol.getName())
                                .status(rol.getStatus())
                                .build())
                        .collect(Collectors.toList()),
                pagingSort, totalElements);
    }
}
