package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.PositionDto;
import com.restaurante.bot.dto.PositionGetAllDto;
import com.restaurante.bot.dto.PositionSaveAndUpdateDto;
import com.restaurante.bot.model.Position;
import com.restaurante.bot.repository.PositionRepository;
import com.restaurante.bot.business.interfaces.IPositionService;
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
public class PositionServiceImpl implements IPositionService {

    private final PositionRepository positionRepository;

    @Override
    @Transactional
    public PositionDto save(PositionSaveAndUpdateDto positionDto) {
        Optional<Position> positionOptional = positionRepository.findByDescription(positionDto.getDescription());
        if (positionOptional.isPresent()) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "La posición ya existe");
        }
        Position entityPosition = new Position();
        BeanUtils.copyProperties(positionDto, entityPosition);
        entityPosition.setStatus(positionDto.getStatus());
        Position newEntityPosition = positionRepository.save(entityPosition);
        return mapPositionDto(newEntityPosition);
    }

    @Override
    @Transactional
    public PositionDto update(Long positionId, PositionSaveAndUpdateDto positionDto) {
        Optional<Position> positionOptional = positionRepository.findById(positionId);
        if (!positionOptional.isPresent()) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "La posición no existe");
        }
        Position entityPosition = positionOptional.get();
        BeanUtils.copyProperties(positionDto, entityPosition);
        entityPosition.setStatus(positionDto.getStatus());
        Position updatedEntityPosition = positionRepository.save(entityPosition);
        return mapPositionDto(updatedEntityPosition);
    }

    @Override
    @Transactional
    public boolean delete(Long positionId) {
        Optional<Position> positionOptional = positionRepository.findById(positionId);
        if (positionOptional.isPresent()) {
            Position entityPosition = positionOptional.get();
            entityPosition.setStatus(Constants.INACTIVE_STATUS);
            positionRepository.save(entityPosition);
            return true;
        }
        return false;
    }

    @Override
    public PositionDto get(Long positionId) {
        Optional<Position> positionOptional = positionRepository.findById(positionId);
        if (positionOptional.isPresent()) {
            return mapPositionDto(positionOptional.get());
        } else {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "La posición no existe");
        }
    }

    @Override
    public Page<PositionGetAllDto> getAll(Map<String, String> customQuery) {
        String orders = "ASC";
        String sortBy = "id";
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
        return mapPagePositionDto(positionRepository.findByStatus(status, pagingSort), pagingSort);
    }

    @Override
    public Page<PositionGetAllDto> getAll(int page, int size, String orders, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(orders);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return mapPagePositionDto(positionRepository.findByStatus(Constants.ACTIVE_STATUS, pagingSort), pagingSort);
    }

    @Override
    public List<PositionGetAllDto> getAllWithOutPage(Map<String, String> customQuery) {
        String status = Constants.ACTIVE_STATUS;
        if (customQuery.containsKey("status")) {
            status = customQuery.get("status");
        }
        return positionRepository.findByStatus(status).stream()
                .map(position -> PositionGetAllDto.builder()
                        .id(position.getPositionId())
                        .description(position.getDescription())
                        .status(position.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<PositionGetAllDto> searchCustom(Map<String, String> customQuery) {
        String orders = "ASC";
        String sortBy = "id";
        int page = 0;
        int size = 5;
        String id = null;
        String description = null;
        String status = Constants.ACTIVE_STATUS;
        if (customQuery.containsKey("id")) {
            id = customQuery.get("id");
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
        return mapPagePositionDto(
                positionRepository.findByIdOrDescriptionContainingIgnoreCaseAndStatus(
                        id, description, status, pagingSort), pagingSort);
    }

    private PositionDto mapPositionDto(Position entityPosition) {
        PositionDto positionDto = new PositionDto();
        BeanUtils.copyProperties(entityPosition, positionDto);
        return positionDto;
    }

    private Page<PositionGetAllDto> mapPagePositionDto(Page<Position> entityPage, Pageable pagingSort) {
        int totalElements = (int) entityPage.getTotalElements();
        return new PageImpl<>(
                entityPage.getContent().stream()
                        .map(position -> PositionGetAllDto.builder()
                                .id(position.getPositionId())
                                .description(position.getDescription())
                                .status(position.getStatus())
                                .build())
                        .collect(Collectors.toList()), pagingSort, totalElements);
    }
}