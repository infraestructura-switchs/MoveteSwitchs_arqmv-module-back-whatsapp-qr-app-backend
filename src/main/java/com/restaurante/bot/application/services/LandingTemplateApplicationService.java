package com.restaurante.bot.application.services;

import com.restaurante.bot.application.ports.incoming.LandingTemplateUseCase;
import com.restaurante.bot.application.ports.outgoing.LandingTemplateRepositoryPort;
import com.restaurante.bot.dto.LandingTemplateRequest;
import com.restaurante.bot.dto.LandingTemplateResponseDTO;
import com.restaurante.bot.model.LandingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service("landingTemplateApplicationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LandingTemplateApplicationService implements LandingTemplateUseCase {
    private final LandingTemplateRepositoryPort repository;

    @Override
    @Transactional
    public LandingTemplateRequest save(LandingTemplateRequest request) {
        LandingTemplate entity = new LandingTemplate();
        entity.setName(request.getName());
        entity.setStatus(request.getStatus() == null ? "ACTIVE" : request.getStatus());
        LandingTemplate saved = repository.save(entity);

        return LandingTemplateRequest.builder()
                .landingTemplateId(saved.getLandingTemplateId())
                .name(saved.getName())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public List<LandingTemplateRequest> getAllLandingTemplate() {
        return repository.getAllLandingTemplate();
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        if (repository.existsById(id)) {
            LandingTemplate lt = repository.findById(id).orElseThrow();
            lt.setStatus("INACTIVE");
            repository.save(lt);
            return true;
        }
        throw new RuntimeException("LandingTemplate no encontrada con id " + id);
    }

    @Override
    @Transactional
    public LandingTemplateRequest update(LandingTemplateRequest request) {
        LandingTemplate lt = repository.findById(request.getLandingTemplateId())
                .orElseThrow(() -> new RuntimeException("LandingTemplate con ID " + request.getLandingTemplateId() + " no existe"));

        if (request.getName() != null) lt.setName(request.getName());
        if (request.getStatus() != null) lt.setStatus(request.getStatus());
        lt.setUpdatedAt(LocalDateTime.now());

        LandingTemplate updated = repository.save(lt);

        return LandingTemplateRequest.builder()
                .landingTemplateId(updated.getLandingTemplateId())
                .name(updated.getName())
                .status(updated.getStatus())
                .build();
    }

    @Override
    public Page<LandingTemplateResponseDTO> getAllPageLandingTemplate(int page, int size, String orders, String sortBy) {
        Sort.Direction direction = Sort.Direction.fromString(orders);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pagingSort = PageRequest.of(page, size, sort);
        return repository.getAllPageLandingTemplate(pagingSort);
    }

    @Override
    public LandingTemplateRequest get(Long id) {
        LandingTemplate lt = repository.findById(id).orElseThrow(() -> new RuntimeException("LandingTemplate no encontrada con id " + id));
        return LandingTemplateRequest.builder()
                .landingTemplateId(lt.getLandingTemplateId())
                .name(lt.getName())
                .status(lt.getStatus())
                .build();
    }

    @Override
    public Page<LandingTemplateResponseDTO> getAll(Map<String, String> customQuery) {
        int page = Integer.parseInt(customQuery.getOrDefault("page", "0"));
        int size = Integer.parseInt(customQuery.getOrDefault("size", "10"));
        String orders = customQuery.getOrDefault("orders", "ASC");
        String sortBy = customQuery.getOrDefault("sortBy", "landingTemplateId");
        return getAllPageLandingTemplate(page, size, orders, sortBy);
    }

    @Override
    public List<LandingTemplateResponseDTO> getAllWithoutPage(Map<String, String> customQuery) {
        List<LandingTemplateRequest> list = repository.getAllLandingTemplate();
        return list.stream().map(l -> LandingTemplateResponseDTO.builder()
                .landingTemplateId(l.getLandingTemplateId())
                .name(l.getName())
                .status(l.getStatus())
                .build()).toList();
    }

    @Override
    public Page<LandingTemplateResponseDTO> searchCustom(Map<String, String> customQuery) {
        return getAll(customQuery);
    }
}
