package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.LandingTemplateRepositoryPort;
import com.restaurante.bot.dto.LandingTemplateRequest;
import com.restaurante.bot.dto.LandingTemplateResponseDTO;
import com.restaurante.bot.model.LandingTemplate;
import com.restaurante.bot.repository.LandingTemplateRepository;
import com.restaurante.bot.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LandingTemplateRepositoryAdapter implements LandingTemplateRepositoryPort {
    private final LandingTemplateRepository landingTemplateRepository;

    @Override
    public LandingTemplate save(LandingTemplate landingTemplate) {
        return landingTemplateRepository.save(landingTemplate);
    }

    @Override
    public Optional<LandingTemplate> findById(Long id) {
        return landingTemplateRepository.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return landingTemplateRepository.existsById(id);
    }

    @Override
    public List<LandingTemplateRequest> getAllLandingTemplate() {
        return landingTemplateRepository.findByStatus(Constants.ACTIVE_STATUS).stream()
                .map(l -> LandingTemplateRequest.builder()
                        .landingTemplateId(l.getLandingTemplateId())
                        .name(l.getName())
                        .status(l.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<LandingTemplateResponseDTO> getAllPageLandingTemplate(Pageable pageable) {
        Page<LandingTemplate> page = landingTemplateRepository.findByStatus(Constants.ACTIVE_STATUS, pageable);
        List<LandingTemplateResponseDTO> content = page.getContent().stream()
                .map(l -> LandingTemplateResponseDTO.builder()
                        .landingTemplateId(l.getLandingTemplateId())
                        .name(l.getName())
                        .status(l.getStatus())
                        .createdAt(l.getCreatedAt())
                        .updatedAt(l.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }
}
