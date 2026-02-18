package com.restaurante.bot.business.interfaces;


import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.dto.CompanyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CompanyInterface {

    @Transactional
    CompanyRequest save(CompanyRequest companyRequest, MultipartFile logoFile);

    List<CompanyRequest> getAllCompany();

    Boolean delete(Long id);

    CompanyRequest update(CompanyRequest companyRequest, MultipartFile logoFile);

    Page<CompanyResponseDTO> getAllPageCompany(int page, int size, String orders, String sortBy);




}
