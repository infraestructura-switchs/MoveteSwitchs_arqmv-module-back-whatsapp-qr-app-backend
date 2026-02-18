package com.restaurante.bot.business.service;


import com.restaurante.bot.dto.AreaDto;
import com.restaurante.bot.dto.AreaGetAllDto;
import com.restaurante.bot.dto.AreaSaveAndUpdateDto;
import com.restaurante.bot.model.Area;
import com.restaurante.bot.repository.AreaRepository;
import com.restaurante.bot.business.interfaces.IAreaService;
import com.restaurante.bot.util.Constants;
import com.restaurante.bot.exception.CustomErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AreaServiceImpl implements IAreaService {

    private final AreaRepository areaRepository;

    @Override
    @Transactional
    public AreaDto save(AreaSaveAndUpdateDto areaDto) {
        Optional<Area> areaOptional = areaRepository.findByDescription(areaDto.getDescription());
        if (areaOptional.isPresent()) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "El área ya existe");
        }

        Area entityArea = new Area();
        BeanUtils.copyProperties(areaDto, entityArea);
        entityArea.setStatus(areaDto.getStatus());

        Area newEntityArea = areaRepository.save(entityArea);
        return mapAreaDto(newEntityArea);
    }

    @Override
    @Transactional
    public AreaDto update(Long areaId, AreaSaveAndUpdateDto areaDto) {
        Optional<Area> areaOptional = areaRepository.findById(areaId);
        if (!areaOptional.isPresent()) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "El área no existe");
        }

        Area entityArea = areaOptional.get();
        BeanUtils.copyProperties(areaDto, entityArea);
        entityArea.setStatus(areaDto.getStatus());

        Area updatedEntityArea = areaRepository.save(entityArea);
        return mapAreaDto(updatedEntityArea);
    }

    @Override
    @Transactional
    public boolean delete(Long areaId) {
        Optional<Area> areaOptional = areaRepository.findById(areaId);
        if (areaOptional.isPresent()) {
            Area entityArea = areaOptional.get();
            entityArea.setStatus(Constants.INACTIVE_STATUS);
            areaRepository.save(entityArea);
            return true;
        }
        return false;
    }

    @Override
    public AreaDto get(Long areaId) {
        Optional<Area> areaOptional = areaRepository.findById(areaId);
        if (areaOptional.isPresent()) {
            return mapAreaDto(areaOptional.get());
        } else {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "El área no existe");
        }
    }

    @Override
    public Page<AreaGetAllDto> getAll(Map<String, String> customQuery) {
        String orders = "ASC";
        String sortBy = "areaId";
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

        return mapPageAreaDto(areaRepository.findByStatus(status, pagingSort), pagingSort);
    }

    @Override
    public Page<AreaGetAllDto> getAll(int page, int size, String orders, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(orders);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return mapPageAreaDto(areaRepository.findByStatus(Constants.ACTIVE_STATUS, pagingSort), pagingSort);
    }

    @Override
    public List<AreaGetAllDto> getAllWithOutPage(Map<String, String> customQuery) {
        String status = Constants.ACTIVE_STATUS;

        if (customQuery.containsKey("status")) {
            status = customQuery.get("status");
        }

        return areaRepository.findByStatus(status).stream()
                .map(area -> AreaGetAllDto.builder()
                        .id(area.getAreaId())
                        .description(area.getDescription())
                        .status(area.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<AreaGetAllDto> searchCustom(Map<String, String> customQuery) {
        String orders = "ASC";
        String sortBy = "areaId";
        int page = 0;
        int size = 5;
        Long id = null;
        String description = null;
        String status = Constants.ACTIVE_STATUS;

        if (customQuery.containsKey("id")) {
            id = Long.valueOf(customQuery.get("id"));
        }
        if (customQuery.containsKey("description")) {
            description = customQuery.get("description");
        }
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

        return mapPageAreaDto(
                areaRepository.findByIdOrDescriptionContainingIgnoreCaseAndStatus(
                        id, description, status, pagingSort), pagingSort);
    }

    private AreaDto mapAreaDto(Area entityArea) {
        AreaDto areaDto = new AreaDto();
        BeanUtils.copyProperties(entityArea, areaDto);
        return areaDto;
    }

    private Page<AreaGetAllDto> mapPageAreaDto(Page<Area> entityPage, Pageable pagingSort) {
        int totalElements = (int) entityPage.getTotalElements();
        return new PageImpl<>(
                entityPage.getContent().stream()
                        .map(area -> AreaGetAllDto.builder()
                                .id(area.getAreaId())
                                .description(area.getDescription())
                                .status(area.getStatus())
                                .build())
                        .collect(Collectors.toList()), pagingSort, totalElements);
    }
}
