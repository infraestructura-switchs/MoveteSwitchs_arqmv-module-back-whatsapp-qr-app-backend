package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CompanyUseCase {
    CompanyRequest save(CompanyRequest companyRequest, MultipartFile logoFile);
    List<CompanyRequest> getAllCompany();
    Boolean delete(Long id);
    CompanyRequest update(CompanyRequest companyRequest, MultipartFile logoFile);
    Page<CompanyResponseDTO> getAllPageCompany(int page, int size, String orders, String sortBy);
    CompanyRequest get(Long id);
    Page<CompanyResponseDTO> getAll(Map<String, String> customQuery);
    List<CompanyResponseDTO> getAllWithoutPage(Map<String, String> customQuery);
    Page<CompanyResponseDTO> searchCustom(Map<String, String> customQuery);
}