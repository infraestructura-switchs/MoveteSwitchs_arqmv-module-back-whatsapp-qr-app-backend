package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.OrderUseCase;
import com.restaurante.bot.dto.OrderDetailsDTO;
import com.restaurante.bot.model.GenericResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderDetailsController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderUseCase orderUseCase;

    @MockBean
    private com.restaurante.bot.util.JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "corsConfigurationSourceImpl")
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @MockBean
    private com.restaurante.bot.util.JwtUtil jwtUtil;

    @Test
    void saveOrder_shouldAcceptExternalCompanyId() throws Exception {
        when(orderUseCase.saveOrder(any(OrderDetailsDTO.class))).thenReturn(new GenericResponse("ok", 200L));

        String payload = """
                {
                  "restaurantTable": 10,
                  "phone": "3001234567",
                  "externalCompanyId": 273,
                  "items": [
                    {
                      "productId": "1",
                      "qty": 1,
                      "unitPrice": 10000
                    }
                  ],
                  "total": 10000
                }
                """;

        mockMvc.perform(post("/api/back-whatsapp-qr-app/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        ArgumentCaptor<OrderDetailsDTO> captor = ArgumentCaptor.forClass(OrderDetailsDTO.class);
        verify(orderUseCase).saveOrder(captor.capture());
        assertEquals(273L, captor.getValue().getExternalCompanyId());
    }
}
