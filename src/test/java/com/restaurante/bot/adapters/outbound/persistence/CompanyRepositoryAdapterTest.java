package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.model.Company;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import com.restaurante.bot.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyRepositoryAdapterTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyRepositoryAdapter adapter;

    @Test
    void save_delegatesToRepository() {
        Company c = new Company();
        c.setId(1L);

        when(companyRepository.save(c)).thenReturn(c);

        Company result = adapter.save(c);

        assertEquals(c, result);
        verify(companyRepository).save(c);
    }

    @Test
    void findById_delegatesToRepository() {
        Company c = new Company();
        c.setId(2L);

        when(companyRepository.findById(2L)).thenReturn(Optional.of(c));

        Optional<Company> result = adapter.findById(2L);

        assertTrue(result.isPresent());
        assertEquals(c, result.get());
        verify(companyRepository).findById(2L);
    }

    @Test
    void existsById_delegatesToRepository() {
        when(companyRepository.existsById(3L)).thenReturn(true);

        boolean exists = adapter.existsById(3L);

        assertTrue(exists);
        verify(companyRepository).existsById(3L);
    }

    @Test
    void getAllCompany_delegatesToRepository() {
        Company c1 = new Company();
        c1.setId(1L);
        c1.setName("Company 1");
        c1.setStatus("ACTIVE");
        Company c2 = new Company();
        c2.setId(2L);
        c2.setName("Company 2");
        c2.setStatus("ACTIVE");
        List<Company> list = Arrays.asList(c1, c2);

        when(companyRepository.findByStatus("ACTIVE")).thenReturn(list);

        List<CompanyRequest> result = adapter.getAllCompany();

        assertEquals(2, result.size());
        verify(companyRepository).findByStatus("ACTIVE");
    }

    @Test
    void getAllPageCompany_delegatesToRepository() {
        Company c1 = new Company();
        c1.setId(1L);
        c1.setName("Company 1");
        c1.setStatus("ACTIVE");
        Page<Company> page = new PageImpl<>(List.of(c1));

        when(companyRepository.findByStatus(eq("ACTIVE"), any(Pageable.class))).thenReturn(page);

        Page<CompanyResponseDTO> result = adapter.getAllPageCompany(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(companyRepository).findByStatus(eq("ACTIVE"), any(Pageable.class));
    }
}
