package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.dto.LandingTemplateRequest;
import com.restaurante.bot.dto.LandingTemplateResponseDTO;
import com.restaurante.bot.model.LandingTemplate;
import com.restaurante.bot.repository.LandingTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LandingTemplateRepositoryAdapterTest {

    @Mock
    private LandingTemplateRepository landingTemplateRepository;

    @InjectMocks
    private LandingTemplateRepositoryAdapter adapter;

    @Test
    void save_find_exists() {
        LandingTemplate lt = new LandingTemplate();

        when(landingTemplateRepository.save(lt)).thenReturn(lt);
        when(landingTemplateRepository.findById(9L)).thenReturn(Optional.of(lt));
        when(landingTemplateRepository.existsById(9L)).thenReturn(true);

        assertEquals(lt, adapter.save(lt));
        assertTrue(adapter.findById(9L).isPresent());
        assertTrue(adapter.existsById(9L));
    }

    @Test
    void getAll_delegates() {
        LandingTemplate landingTemplate = new LandingTemplate();
        landingTemplate.setLandingTemplateId(1L);
        landingTemplate.setName("Template 1");
        landingTemplate.setStatus("ACTIVE");
        Page<LandingTemplate> page = new PageImpl<>(List.of(landingTemplate));

        when(landingTemplateRepository.findByStatusAndStatusNot("ACTIVE", "DELETED")).thenReturn(List.of(landingTemplate));
        when(landingTemplateRepository.findByStatusAndStatusNot(eq("ACTIVE"), eq("DELETED"), any(Pageable.class))).thenReturn(page);

        assertEquals(1, adapter.getAllLandingTemplate().size());
        assertEquals(1, adapter.getAllPageLandingTemplate(Pageable.unpaged()).getTotalElements());
        verify(landingTemplateRepository).findByStatusAndStatusNot("ACTIVE", "DELETED");
        verify(landingTemplateRepository).findByStatusAndStatusNot(eq("ACTIVE"), eq("DELETED"), any(Pageable.class));
    }
}
