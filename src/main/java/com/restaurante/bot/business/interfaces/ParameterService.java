package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.ParameterRequestDTO;
import com.restaurante.bot.dto.ParameterResponseDTO;
import com.restaurante.bot.model.Parameter;

import java.util.List;

public interface ParameterService {
    
    List<ParameterResponseDTO> getAllParameters();
    
    List<ParameterResponseDTO> getParametersByCompanyId(Long companyId);
    
    ParameterResponseDTO getParameterById(Long id);
    
    ParameterResponseDTO getParameterByName(String name);
    
    ParameterResponseDTO getParameterByNameAndCompanyId(String name, Long companyId);
    
    ParameterResponseDTO createParameter(ParameterRequestDTO request);
    
    ParameterResponseDTO updateParameter(Long id, ParameterRequestDTO request);
    
    void deleteParameter(Long id);
    
    List<ParameterResponseDTO> getParametersByStatus(String status);
    
    List<ParameterResponseDTO> getParametersByCompanyIdAndStatus(Long companyId, String status);
}