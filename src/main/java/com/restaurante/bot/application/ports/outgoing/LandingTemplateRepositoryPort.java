package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.LandingTemplate;
import com.restaurante.bot.dto.LandingTemplateRequest;
import com.restaurante.bot.dto.LandingTemplateResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LandingTemplateRepositoryPort {
    LandingTemplate save(LandingTemplate landingTemplate);
    Optional<LandingTemplate> findById(Long id);
    boolean existsById(Long id);
    List<LandingTemplateRequest> getAllLandingTemplate();
    Page<LandingTemplateResponseDTO> getAllPageLandingTemplate(Pageable pageable);
}
