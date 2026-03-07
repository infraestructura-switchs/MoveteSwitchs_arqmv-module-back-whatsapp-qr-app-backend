package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.Parameter;

import java.util.List;
import java.util.Optional;

public interface ParameterRepositoryPort {
    List<Parameter> findAll();
    List<Parameter> findByCompanyId(Long companyId);
    Optional<Parameter> findById(Long id);
    Optional<Parameter> findByName(String name);
    Optional<Parameter> findByNameAndCompanyId(String name, Long companyId);
    List<Parameter> findByStatus(String status);
    List<Parameter> findByCompanyIdAndStatus(Long companyId, String status);
    Parameter save(Parameter parameter);
    void deleteById(Long id);
    boolean existsByNameAndCompanyId(String name, Long companyId);
}