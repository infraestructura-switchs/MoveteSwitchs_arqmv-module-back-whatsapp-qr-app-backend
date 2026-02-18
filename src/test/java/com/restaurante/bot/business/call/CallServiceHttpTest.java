package com.restaurante.bot.business.call;

import com.restaurante.bot.api.definition.ServiceCallProductsApi;
import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.config.ApiConfig;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallServiceHttpTest {

    @Mock
    private ApiConfig apiConfig;

    @Mock
    private ServiceCallProductsApi serviceCallProductsApi;

    @Mock
    private Call<List<ProductDTO>> call;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CallServiceHttp callServiceHttp;

    private ProductDTO mockProduct;
    private Company mockCompany;

    @BeforeEach
    void setUp() {
        mockProduct = new ProductDTO();
        mockProduct.setId(1L);
        mockProduct.setIdProducto("PROD001");

        mockCompany = new Company();
        mockCompany.setApiKey("23568183-c1ce-2fa9-e063-f501000e7fea");
    }

    @Test
    void getProduct_ShouldReturnProductList_WhenSuccessful() throws IOException {
        Long companyId = 1L;
        List<ProductDTO> expectedProducts = Arrays.asList(mockProduct);

        when(companyRepository.findByExternalCompanyId(companyId)).thenReturn(mockCompany);
        when(apiConfig.getAllProduct()).thenReturn(serviceCallProductsApi);
        when(serviceCallProductsApi.getAllProduct(eq(mockCompany.getApiKey()), eq(companyId))).thenReturn(call);
        when(call.execute()).thenReturn(Response.success(expectedProducts));

        List<ProductDTO> result = callServiceHttp.getProduct(companyId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PROD001", result.get(0).getIdProducto());

        verify(companyRepository, times(1)).findByExternalCompanyId(companyId);
        verify(apiConfig, times(1)).getAllProduct();
        verify(serviceCallProductsApi, times(1)).getAllProduct(eq(mockCompany.getApiKey()), eq(companyId));
    }

    @Test
    void getProduct_ShouldReturnEmptyList_WhenNoDataFound() throws IOException {
        Long companyId = 1L;
        when(companyRepository.findByExternalCompanyId(companyId)).thenReturn(mockCompany);
        when(apiConfig.getAllProduct()).thenReturn(serviceCallProductsApi);
        when(serviceCallProductsApi.getAllProduct(eq(mockCompany.getApiKey()), eq(companyId))).thenReturn(call);

        okhttp3.ResponseBody errorBody = okhttp3.ResponseBody.create(
                okhttp3.MediaType.parse("application/json"), "No data found");
        when(call.execute()).thenReturn(Response.error(HttpStatus.NOT_FOUND.value(), errorBody));

        List<ProductDTO> result = callServiceHttp.getProduct(companyId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(companyRepository, times(1)).findByExternalCompanyId(companyId);
        verify(apiConfig, times(1)).getAllProduct();
        verify(serviceCallProductsApi, times(1)).getAllProduct(eq(mockCompany.getApiKey()), eq(companyId));
    }

    @Test
    void getProduct_ShouldReturnEmptyList_WhenCompanyNotFound() {
        Long companyId = 1L;
        when(companyRepository.findByExternalCompanyId(companyId)).thenReturn(null);

        List<ProductDTO> result = callServiceHttp.getProduct(companyId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(companyRepository, times(1)).findByExternalCompanyId(companyId);
        verifyNoInteractions(apiConfig, serviceCallProductsApi, call);
    }

    @Test
    void getProduct_ShouldThrowException_WhenApiCallFailsWithIOException() throws IOException {
        Long companyId = 1L;
        when(companyRepository.findByExternalCompanyId(companyId)).thenReturn(mockCompany);
        when(apiConfig.getAllProduct()).thenReturn(serviceCallProductsApi);
        when(serviceCallProductsApi.getAllProduct(eq(mockCompany.getApiKey()), eq(companyId))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("API Error"));

        assertThrows(GenericException.class, () -> {
            callServiceHttp.getProduct(companyId);
        });

        verify(companyRepository, times(1)).findByExternalCompanyId(companyId);
        verify(apiConfig, times(1)).getAllProduct();
        verify(serviceCallProductsApi, times(1)).getAllProduct(eq(mockCompany.getApiKey()), eq(companyId));
    }
}