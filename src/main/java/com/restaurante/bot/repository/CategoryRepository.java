package com.restaurante.bot.repository;

import com.restaurante.bot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    Optional<Category> findByNameAndCompanyId(String name, Long companyId);
    
    List<Category> findByStatus(String status);
    
    //List<Category> findByParameterParameterId(Long parameterId);

    Optional<Category> findByExternalId(Long externalId);
    
    List<Category> findByNameContainingIgnoreCase(String name);
    
    List<Category> findByCompanyId(Long companyId);
    
    List<Category> findByCompanyIdAndStatus(Long companyId, String status);
    
    List<Category> findByCompanyIdAndExternalId(Long companyId, Long parameterId);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndCompanyId(String name, Long companyId);


    Optional<Category> findByCompanyIdAndNameIgnoreCase(Long companyId, String name);
}