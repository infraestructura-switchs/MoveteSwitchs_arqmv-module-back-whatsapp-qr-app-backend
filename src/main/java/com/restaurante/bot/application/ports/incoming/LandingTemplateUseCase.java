package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.dto.LandingTemplateRequest;
import com.restaurante.bot.dto.LandingTemplateResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface LandingTemplateUseCase {
    LandingTemplateRequest save(LandingTemplateRequest request);
    List<LandingTemplateRequest> getAllLandingTemplate();
    Boolean delete(Long id);
    LandingTemplateRequest update(LandingTemplateRequest request);
    Page<LandingTemplateResponseDTO> getAllPageLandingTemplate(int page, int size, String orders, String sortBy);
    LandingTemplateRequest get(Long id);
    Page<LandingTemplateResponseDTO> getAll(Map<String, String> customQuery);
    List<LandingTemplateResponseDTO> getAllWithoutPage(Map<String, String> customQuery);
    Page<LandingTemplateResponseDTO> searchCustom(Map<String, String> customQuery);
}
