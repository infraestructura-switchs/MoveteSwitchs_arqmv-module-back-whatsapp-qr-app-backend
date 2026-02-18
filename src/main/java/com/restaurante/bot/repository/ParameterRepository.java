package com.restaurante.bot.repository;

import com.restaurante.bot.model.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    
    Optional<Parameter> findByName(String name);
    
    List<Parameter> findByStatus(String status);
    
    List<Parameter> findByCompanyId(Long companyId);
    
    List<Parameter> findByCompanyIdAndStatus(Long companyId, String status);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndCompanyId(String name, Long companyId);

    Optional<Parameter> findByNameAndCompanyId(String name, Long companyId);
}