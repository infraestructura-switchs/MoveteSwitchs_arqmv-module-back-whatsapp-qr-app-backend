package com.restaurante.bot.business.call;

import com.restaurante.bot.api.definition.ServiceCallProductsApi;
import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.config.ApiConfig;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallServiceHttp {

    private final ApiConfig apiConfig;
    private final CompanyRepository companyRepository;

    public List<ProductDTO> getProduct(Long companyId) {
        Company company = companyRepository.findByExternalCompanyId(companyId);
        if (company == null) {
            log.warn("No se encontró compañía con externalCompanyId: {}", companyId);
            return Collections.emptyList();
        }

        try {
            ServiceCallProductsApi service = apiConfig.getAllProduct();
            Call<List<ProductDTO>> call = service.getAllProduct(company.getApiKey(), companyId);
            Response<List<ProductDTO>> response = call.execute();

            if (response.isSuccessful()) {
                List<ProductDTO> products = response.body();
                log.info("El servicio de obtener los productos respondió para companyId {} -> {}", companyId, products);
                return products != null ? products : Collections.emptyList();
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin cuerpo de error";
                log.warn("El servicio de obtener los productos respondió con error para companyId {} -> code: {}, message: {}, headers: {}",
                        companyId, response.code(), errorBody, response.headers());
                return Collections.emptyList(); // Retorna lista vacía para cualquier error HTTP
            }
        } catch (IOException e) {
            log.error("Error de conexión al llamar al servicio para companyId {} -> {}", companyId, e.getMessage());
            throw new GenericException("Ocurrió un error de conexión al consultar los productos para companyId " + companyId,
                    HttpStatus.BAD_REQUEST);
        }
    }
}