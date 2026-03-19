package com.restaurante.bot.controller;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.dto.ProductSaveAndUpdateDto;
import com.restaurante.bot.application.ports.incoming.CompanyUseCase;
import com.restaurante.bot.dto.CompanyRequest;
import org.springframework.data.domain.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Disabled("Flaky integration-style smoke test; enable when running full integration environment")
class CompanyProductPostmanCollectionSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Cloudinary cloudinary;

    @MockBean
    private CallServiceHttp callServiceHttp;

        @MockBean
        private CompanyUseCase companyUseCase;

    @Test
    void postmanCollectionRequests_ShouldNotReturnServerErrors() throws Exception {
        when(callServiceHttp.getProduct(org.mockito.ArgumentMatchers.anyLong())).thenReturn(List.<ProductDTO>of());

        // Mock CompanyUseCase to avoid DB dependencies and prevent NPEs
        CompanyRequest saved = CompanyRequest.builder()
                .companyId(1L)
                .externalCompanyId(123L)
                .apiKey("test-api-key")
                .build();
        when(companyUseCase.save(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(saved);
        when(companyUseCase.getAllCompany()).thenReturn(List.of(saved));
        when(companyUseCase.get(org.mockito.ArgumentMatchers.anyLong())).thenReturn(saved);
        when(companyUseCase.getAllPageCompany(org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(Page.empty());
        when(companyUseCase.getAllWithoutPage(org.mockito.ArgumentMatchers.any())).thenReturn(List.of());
        when(companyUseCase.searchCustom(org.mockito.ArgumentMatchers.any())).thenReturn(Page.empty());

        Long companyId = createCompany();
        assertNotNull(companyId);

        expectNotServerError(get("/api/back-whatsapp-qr-app/company/get-company"));
        expectNotServerError(get("/api/back-whatsapp-qr-app/company/" + companyId));
        expectNotServerError(delete("/api/back-whatsapp-qr-app/company/delete/" + companyId));
        expectNotServerError(multipart("/api/back-whatsapp-qr-app/company/updateByCompanynId/" + companyId)
                .param("companyName", "Mi Empresa Modificada")
                .param("apiKey", "test-api-key")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }));
        expectNotServerError(get("/api/back-whatsapp-qr-app/company").param("page", "0").param("size", "10"));
        expectNotServerError(get("/api/back-whatsapp-qr-app/company/search").param("name", "Mi Empresa"));

        int getProductsByCompanyStatus = expectNotServerError(get("/api/back-whatsapp-qr-app/product/getProductByCompany/" + companyId));
        assertTrue(getProductsByCompanyStatus == 200 || getProductsByCompanyStatus == 401 || getProductsByCompanyStatus == 404,
                "Get Products By Company should be controlled, not 5xx");

        expectNotServerError(post("/api/back-whatsapp-qr-app/product/update-data").param("companyId", companyId.toString()));

        Long productId = createProduct(companyId);
        assertNotNull(productId);

        expectNotServerError(put("/api/back-whatsapp-qr-app/product/updateDescription")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"productId\": " + productId + ", \"description\": \"Nueva descripción\" }"));
        expectNotServerError(get("/api/back-whatsapp-qr-app/product/search")
                .param("companyId", companyId.toString())
                .param("name", "Pizza")
                .param("category", "Comidas"));
        expectNotServerError(get("/api/back-whatsapp-qr-app/product/by-price")
                .param("companyId", companyId.toString())
                .param("sort", "asc"));

        expectNotServerError(get("/api/back-whatsapp-qr-app/admin/product/" + productId));
        expectNotServerError(put("/api/back-whatsapp-qr-app/admin/product/update/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ProductSaveAndUpdateDto.builder()
                        .productName("Pizza Hawaiana")
                        .price(13000.0)
                        .companyId(companyId)
                        .build())));
        expectNotServerError(delete("/api/back-whatsapp-qr-app/admin/product/delete/" + productId));
        expectNotServerError(get("/api/back-whatsapp-qr-app/admin/product")
                .param("companyId", companyId.toString())
                .param("page", "0")
                .param("size", "5"));
        expectNotServerError(get("/api/back-whatsapp-qr-app/admin/product/get-all-without-page")
                .param("companyId", companyId.toString()));
        expectNotServerError(get("/api/back-whatsapp-qr-app/admin/product/search")
                .param("companyId", companyId.toString())
                .param("name", "Pizza"));
    }

    private Long createCompany() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/back-whatsapp-qr-app/company/create")
                        .param("companyName", "Mi Empresa")
                        .param("whatsappNumber", "+573001234567")
                        .param("longitude", "-74.0")
                        .param("latitude", "4.6")
                        .param("baseValue", "0.0")
                        .param("additionalValue", "0.0")
                        .param("externalId", "123")
                        .param("cityId", "1")
                        .param("apiKey", "test-api-key"))
                .andReturn();

        assertTrue(result.getResponse().getStatus() < 500);
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("companyId").asLong();
    }

    private Long createProduct(Long companyId) throws Exception {
        ProductSaveAndUpdateDto request = ProductSaveAndUpdateDto.builder()
                .productName("Pizza")
                .price(12000.0)
                .companyId(companyId)
                .build();

        MvcResult result = mockMvc.perform(post("/api/back-whatsapp-qr-app/admin/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertTrue(result.getResponse().getStatus() < 500);
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private int expectNotServerError(org.springframework.test.web.servlet.RequestBuilder requestBuilder) throws Exception {
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        int status = result.getResponse().getStatus();
        String responseBody = result.getResponse().getContentAsString();
        assertTrue(status < 500,
                () -> "Unexpected 5xx for request. Status=" + status + ", body=" + responseBody);
        return status;
    }
}