package com.restaurante.bot.business.service;

import com.cloudinary.Cloudinary;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private Cloudinary cloudinary;

    @InjectMocks
    private CompanyService companyService;

    private Company mockCompany;
    private CompanyRequest mockCompanyRequest;

    @BeforeEach
    void setUp() {
        mockCompany = new Company();
        mockCompany.setId(1L);
        mockCompany.setName("Test Company");
        mockCompany.setLogo("test-logo.jpg");
        mockCompany.setNumberWhatsapp("1234567890");
        mockCompany.setStatus("ACTIVE");

        mockCompanyRequest = new CompanyRequest();
        mockCompanyRequest.setNameCompany("Test Company");
        mockCompanyRequest.setNumberWhatsapp("1234567890");
    }

    @Test
    void getAllCompany_ShouldReturnCompanyList() {
        // Given
        List<CompanyRequest> mockCompanies = Arrays.asList(mockCompanyRequest);
        when(companyRepository.getAllCompany()).thenReturn(mockCompanies);

        // When
        List<CompanyRequest> result = companyService.getAllCompany();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Company", result.get(0).getNameCompany());
        
        verify(companyRepository, times(1)).getAllCompany();
    }

    @Test
    void save_ShouldSaveCompanyWithLogo() throws IOException {
        // Given
        MockMultipartFile logoFile = new MockMultipartFile(
            "logo", "test-logo.jpg", "image/jpeg", "test image".getBytes()
        );
        
        Map<String, Object> uploadResult = Map.of("url", "https://cloudinary.com/test-logo.jpg");
        
        when(cloudinary.uploader()).thenReturn(mock(com.cloudinary.Uploader.class));
        when(cloudinary.uploader().upload(any(byte[].class), any())).thenReturn(uploadResult);
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompany);

        // When
        CompanyRequest result = companyService.save(mockCompanyRequest, logoFile);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getCompanyId());
        assertEquals("test-logo.jpg", result.getLogoUrl());
        
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void delete_ShouldSetStatusInactive_WhenCompanyExists() {
        // Given
        Long companyId = 1L;
        when(companyRepository.existsById(companyId)).thenReturn(true);
        when(companyRepository.findById(companyId)).thenReturn(java.util.Optional.of(mockCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompany);

        // When
        Boolean result = companyService.delete(companyId);

        // Then
        assertTrue(result);
        assertEquals("INACTIVE", mockCompany.getStatus());
        
        verify(companyRepository, times(1)).existsById(companyId);
        verify(companyRepository, times(1)).findById(companyId);
        verify(companyRepository, times(1)).save(mockCompany);
    }

    @Test
    void delete_ShouldThrowException_WhenCompanyNotFound() {
        // Given
        Long companyId = 999L;
        when(companyRepository.existsById(companyId)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            companyService.delete(companyId);
        });
        
        verify(companyRepository, times(1)).existsById(companyId);
        verify(companyRepository, never()).findById(any());
        verify(companyRepository, never()).save(any());
    }
}