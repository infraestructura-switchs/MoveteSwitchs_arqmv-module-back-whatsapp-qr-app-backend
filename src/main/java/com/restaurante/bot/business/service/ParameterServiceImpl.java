package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.ParameterService;
import com.restaurante.bot.dto.ParameterRequestDTO;
import com.restaurante.bot.dto.ParameterResponseDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Parameter;
import com.restaurante.bot.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    private final ParameterRepository parameterRepository;

    @Override
    public List<ParameterResponseDTO> getAllParameters() {
        return parameterRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParameterResponseDTO> getParametersByCompanyId(Long companyId) {
        return parameterRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ParameterResponseDTO getParameterById(Long id) {
        Parameter parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new GenericException("Parameter not found", HttpStatus.NOT_FOUND));
        return mapToResponseDTO(parameter);
    }

    @Override
    public ParameterResponseDTO getParameterByName(String name) {
        Parameter parameter = parameterRepository.findByName(name)
                .orElseThrow(() -> new GenericException("Parameter not found", HttpStatus.NOT_FOUND));
        return mapToResponseDTO(parameter);
    }

    @Override
    public ParameterResponseDTO getParameterByNameAndCompanyId(String name, Long companyId) {
        Parameter parameter = parameterRepository.findByNameAndCompanyId(name, companyId)
                .orElseThrow(() -> new GenericException("Parameter not found", HttpStatus.NOT_FOUND));
        return mapToResponseDTO(parameter);
    }

    @Override
    public ParameterResponseDTO createParameter(ParameterRequestDTO request) {
        if (parameterRepository.findByNameAndCompanyId(request.getName(), request.getCompanyId()).isPresent()) {
            throw new GenericException("Parameter with this name already exists", HttpStatus.CONFLICT);
        }

        Parameter parameter = new Parameter();
        parameter.setName(request.getName());
        parameter.setValue(request.getValue());
        parameter.setStatus(request.getStatus());
        parameter.setCompanyId(request.getCompanyId());

        Parameter savedParameter = parameterRepository.save(parameter);
        return mapToResponseDTO(savedParameter);
    }

    @Override
    public ParameterResponseDTO updateParameter(Long id, ParameterRequestDTO request) {
        Parameter parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new GenericException("Parameter not found", HttpStatus.NOT_FOUND));

        // Check if name already exists for this company (excluding current parameter)
        if (!parameter.getName().equals(request.getName()) && 
            parameterRepository.findByNameAndCompanyId(request.getName(), request.getCompanyId()).isPresent()) {
            throw new GenericException("Parameter with this name already exists", HttpStatus.CONFLICT);
        }

        parameter.setName(request.getName());
        parameter.setValue(request.getValue());
        parameter.setStatus(request.getStatus());
        parameter.setCompanyId(request.getCompanyId());

        Parameter updatedParameter = parameterRepository.save(parameter);
        return mapToResponseDTO(updatedParameter);
    }

    @Override
    public void deleteParameter(Long id) {
        if (!parameterRepository.existsById(id)) {
            throw new GenericException("Parameter not found", HttpStatus.NOT_FOUND);
        }
        parameterRepository.deleteById(id);
    }

    @Override
    public List<ParameterResponseDTO> getParametersByStatus(String status) {
        return parameterRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParameterResponseDTO> getParametersByCompanyIdAndStatus(Long companyId, String status) {
        return parameterRepository.findByCompanyIdAndStatus(companyId, status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ParameterResponseDTO mapToResponseDTO(Parameter parameter) {
        return ParameterResponseDTO.builder()
                .parameterId(parameter.getParameterId())
                .name(parameter.getName())
                .value(parameter.getValue())
                .status(parameter.getStatus())
                .companyId(parameter.getCompanyId())
                .createdAt(parameter.getCreatedAt())
                .updatedAt(parameter.getUpdatedAt())
                .build();
    }
}