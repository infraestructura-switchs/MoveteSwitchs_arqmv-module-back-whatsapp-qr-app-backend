package com.restaurante.bot.business.service;

import com.cloudinary.Cloudinary;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.CityRepository;
import com.restaurante.bot.util.StatusConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    @Mock
    private CityRepository cityRepository;

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
    }

    @Test
    void getAllCompany_ShouldReturnCompanyList() {
        // Given
        List<Company> mockCompanies = Arrays.asList(mockCompany);
        when(companyRepository.findByStatusAndStatusNot(StatusConstants.ACTIVE_STATUS, StatusConstants.DELETED_STATUS)).thenReturn(mockCompanies);

        // When
        List<CompanyRequest> result = companyService.getAllCompany();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Company", result.get(0).getNameCompany());
        
        verify(companyRepository, times(1)).findByStatusAndStatusNot(StatusConstants.ACTIVE_STATUS, StatusConstants.DELETED_STATUS);
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
    void delete_ShouldSetStatusDeleted_WhenCompanyExists() {
        // Given
        Long companyId = 1L;
        when(companyRepository.findByIdAndStatusNot(companyId, StatusConstants.DELETED_STATUS)).thenReturn(java.util.Optional.of(mockCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompany);

        // When
        Boolean result = companyService.delete(companyId);

        // Then
        assertTrue(result);
        assertEquals(StatusConstants.DELETED_STATUS, mockCompany.getStatus());
        
        verify(companyRepository, times(1)).findByIdAndStatusNot(companyId, StatusConstants.DELETED_STATUS);
        verify(companyRepository, times(1)).save(mockCompany);
    }

    @Test
    void delete_ShouldThrowException_WhenCompanyNotFound() {
        // Given
        Long companyId = 999L;
        when(companyRepository.findByIdAndStatusNot(companyId, StatusConstants.DELETED_STATUS)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(DomainException.class, () -> {
            companyService.delete(companyId);
        });
        
        verify(companyRepository, times(1)).findByIdAndStatusNot(companyId, StatusConstants.DELETED_STATUS);
        verify(companyRepository, never()).save(any());
    }
}