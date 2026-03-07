package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.ParameterRepositoryPort;
import com.restaurante.bot.model.Parameter;
import com.restaurante.bot.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ParameterRepositoryAdapter implements ParameterRepositoryPort {
    private final ParameterRepository parameterRepository;

    @Override
    public List<Parameter> findAll() {
        return parameterRepository.findAll();
    }

    @Override
    public List<Parameter> findByCompanyId(Long companyId) {
        return parameterRepository.findByCompanyId(companyId);
    }

    @Override
    public Optional<Parameter> findById(Long id) {
        return parameterRepository.findById(id);
    }

    @Override
    public Optional<Parameter> findByName(String name) {
        return parameterRepository.findByName(name);
    }

    @Override
    public Optional<Parameter> findByNameAndCompanyId(String name, Long companyId) {
        return parameterRepository.findByNameAndCompanyId(name, companyId);
    }

    @Override
    public List<Parameter> findByStatus(String status) {
        return parameterRepository.findByStatus(status);
    }

    @Override
    public List<Parameter> findByCompanyIdAndStatus(Long companyId, String status) {
        return parameterRepository.findByCompanyIdAndStatus(companyId, status);
    }

    @Override
    public Parameter save(Parameter parameter) {
        return parameterRepository.save(parameter);
    }

    @Override
    public void deleteById(Long id) {
        parameterRepository.deleteById(id);
    }

    @Override
    public boolean existsByNameAndCompanyId(String name, Long companyId) {
        return parameterRepository.existsByNameAndCompanyId(name, companyId);
    }
}