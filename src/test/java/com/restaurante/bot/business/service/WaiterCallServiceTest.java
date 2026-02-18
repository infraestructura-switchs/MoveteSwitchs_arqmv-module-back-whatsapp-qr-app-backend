package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.WaiterCallRequest;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.RestaurantTable;
import com.restaurante.bot.model.WaiterCall;
import com.restaurante.bot.repository.RestaurantTableRepository;
import com.restaurante.bot.repository.WaiterCallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaiterCallServiceTest {

    @Mock
    private WaiterCallRepository waiterCallRepository;

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @InjectMocks
    private WaiterCallService waiterCallService;

    private WaiterCall mockWaiterCall;
    private RestaurantTable mockTable;
    private WaiterCallRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockTable = new RestaurantTable();
        mockTable.setTableId(1);
        mockTable.setTableNumber(5L);

        mockWaiterCall = new WaiterCall();
        mockWaiterCall.setCallId(1);
        mockWaiterCall.setTableId(1);
        mockWaiterCall.setStatus(1); // 1 for PENDING
        mockWaiterCall.setTime(LocalDateTime.now());

        mockRequest = new WaiterCallRequest();
        mockRequest.setTableId(1);
        mockRequest.setStatus(1);
    }

    @Test
    void getWaiterCalls_ShouldReturnAllCalls() {
        // Given
        when(waiterCallRepository.findAll()).thenReturn(Arrays.asList(mockWaiterCall));

        // When
        List<WaiterCall> result = waiterCallService.getWaiterCalls();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStatus());
        
        verify(waiterCallRepository, times(1)).findAll();
    }

    @Test
    void createWaiterCall_ShouldCreateNewCall() {
        // Given
        when(restaurantTableRepository.findByTableId(1)).thenReturn(Optional.of(mockTable));
        when(waiterCallRepository.save(any(WaiterCall.class))).thenReturn(mockWaiterCall);

        // When
        WaiterCall result = waiterCallService.createWaiterCall(mockRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getStatus());
        assertEquals(1, result.getTableId());
        
        verify(restaurantTableRepository, times(1)).findByTableId(1);
        verify(waiterCallRepository, times(1)).save(any(WaiterCall.class));
    }

    @Test
    void createWaiterCall_ShouldThrowException_WhenTableNotFound() {
        // Given
        when(restaurantTableRepository.findByTableId(1)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GenericException.class, () -> {
            waiterCallService.createWaiterCall(mockRequest);
        });
        
        verify(restaurantTableRepository, times(1)).findByTableId(1);
        verify(waiterCallRepository, never()).save(any());
    }
}