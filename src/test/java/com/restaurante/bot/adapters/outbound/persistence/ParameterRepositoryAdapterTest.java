package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.model.Parameter;
import com.restaurante.bot.repository.ParameterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParameterRepositoryAdapterTest {

    @Mock
    private ParameterRepository parameterRepository;

    @InjectMocks
    private ParameterRepositoryAdapter adapter;

    @Test
    void findAll_and_findByCompanyId() {
        Parameter p1 = new Parameter();
        Parameter p2 = new Parameter();
        List<Parameter> list = Arrays.asList(p1, p2);

        when(parameterRepository.findAll()).thenReturn(list);
        when(parameterRepository.findByCompanyId(1L)).thenReturn(list);

        assertEquals(2, adapter.findAll().size());
        assertEquals(2, adapter.findByCompanyId(1L).size());
        verify(parameterRepository).findAll();
        verify(parameterRepository).findByCompanyId(1L);
    }

    @Test
    void save_delete_existsAndFindById() {
        Parameter p = new Parameter();

        when(parameterRepository.findById(7L)).thenReturn(Optional.of(p));
        when(parameterRepository.save(p)).thenReturn(p);
        when(parameterRepository.existsByNameAndCompanyId("N", 2L)).thenReturn(true);

        assertTrue(adapter.findById(7L).isPresent());
        assertEquals(p, adapter.save(p));
        adapter.deleteById(7L);
        verify(parameterRepository).deleteById(7L);
        assertTrue(adapter.existsByNameAndCompanyId("N", 2L));
    }
}
