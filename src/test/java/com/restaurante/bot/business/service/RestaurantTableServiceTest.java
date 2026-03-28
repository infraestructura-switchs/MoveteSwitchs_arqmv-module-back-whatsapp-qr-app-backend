package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.ChangeStatusTableDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.RestaurantTable;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.RestaurantTableRepository;
import com.restaurante.bot.repository.SubscriptionRepository;
import com.restaurante.bot.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantTableServiceTest {

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private RestaurantTableService restaurantTableService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void changeStatusOcupedShouldReturnBadRequestWhenTableDoesNotExist() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(326L, null, List.of()));

        Company company = Company.builder()
                .id(56L)
                .externalCompanyId(326L)
                .build();

        ChangeStatusTableDTO request = ChangeStatusTableDTO.builder()
                .externalCompanyId(999L)
                .tableNumber(10L)
                .build();

        when(companyRepository.existsByExternalCompanyId(326L)).thenReturn(true);
        when(companyRepository.findByExternalCompanyId(326L)).thenReturn(company);
        when(restaurantTableRepository.findByTableNumberAndCompanyId(10L, 56L)).thenReturn(null);

        GenericException exception = assertThrows(
                GenericException.class,
                () -> restaurantTableService.changeStatusOcuped(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Mesa no resgistrada en la base de datos", exception.getMessage());
        verify(restaurantTableRepository, never()).save(any(RestaurantTable.class));
        verify(notificationService, never()).sendNotificationToClient(any(), any(), any());
    }

    @Test
    void changeStatusOcupedShouldUpdateStatusUsingAuthenticatedCompany() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(326L, null, List.of()));

        Company company = Company.builder()
                .id(56L)
                .externalCompanyId(326L)
                .build();

        RestaurantTable table = new RestaurantTable(1, 10L, 1L, 56L);

        ChangeStatusTableDTO request = ChangeStatusTableDTO.builder()
                .externalCompanyId(999L)
                .tableNumber(10L)
                .build();

        when(companyRepository.existsByExternalCompanyId(326L)).thenReturn(true);
        when(companyRepository.findByExternalCompanyId(326L)).thenReturn(company);
        when(restaurantTableRepository.findByTableNumberAndCompanyId(10L, 56L)).thenReturn(table);
        when(restaurantTableRepository.save(table)).thenReturn(table);

        RestaurantTable result = restaurantTableService.changeStatusOcuped(request);

        assertEquals(2L, result.getStatus());
        assertEquals(56L, result.getCompanyId());
        verify(restaurantTableRepository).save(table);
        verify(notificationService, never()).sendNotificationToClient(any(), any(), any());
    }
}