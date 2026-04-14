package com.restaurante.bot.repository;

import com.restaurante.bot.model.LandingTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface LandingTemplateRepository extends JpaRepository<LandingTemplate, Long> {

    List<LandingTemplate> findByStatus(String status);

    Page<LandingTemplate> findByStatus(String status, Pageable pageable);
}
