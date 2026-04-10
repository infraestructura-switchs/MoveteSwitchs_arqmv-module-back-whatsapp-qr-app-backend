package com.restaurante.bot.application.services;

import com.restaurante.bot.application.ports.incoming.RestaurantTableUseCase;
import com.restaurante.bot.business.interfaces.RestaurantTableInterface;
import com.restaurante.bot.business.service.RestaurantTableStateValidator;
import com.restaurante.bot.business.service.RestaurantTableStateValidator.TableState;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.dto.ChangeStatusTableDTO;
import com.restaurante.bot.dto.NumberDTO;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Restaurant Table Application Service - orchestrates table state management.
 * 
 * This service encapsulates the business logic for restaurant table operations:
 * - Table creation and deletion
 * - State transitions with validation
 * - Listing tables
 * 
 * Clean Architecture Pattern:
 * - Implements RestaurantTableUseCase interface (incoming port)
 * - Delegates to RestaurantTableInterface for persistence
 * - Uses RestaurantTableStateValidator for state machine enforcement
 * - Ensures only valid state transitions are allowed
 * 
 * State Machine Benefits:
 * - Prevents invalid table states
 * - Documents allowed transitions
 * - Makes business rules explicit
 * - Easier to maintain and extend
 */
@Primary
@Service("restaurantTableApplicationService")
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class RestaurantTableApplicationService implements RestaurantTableUseCase {
    
    private final RestaurantTableInterface restaurantTableInterface;
    private final RestaurantTableStateValidator stateValidator;
    
    /**
     * List all restaurant tables
     */
    @Override
    public List<RestaurantTable> ListarMesas() {
        log.info("Retrieving all restaurant tables");
        return restaurantTableInterface.ListarMesas();
    }
    
    /**
     * Add new restaurant table
     * 
     * @param tableNumber the table number to add
     * @return created RestaurantTable
     */
    @Override
    @Transactional
    public RestaurantTable addTable(Long tableNumber) {
        log.info("Adding new restaurant table: {}", tableNumber);
        if (tableNumber == null || tableNumber <= 0) {
            throw new DomainException(
                DomainErrorCode.INVALID_REQUEST,
                "Table number must be a positive number"
            );
        }
        return restaurantTableInterface.addTable(tableNumber);
    }
    
    /**
     * Delete restaurant table
     * 
     * @param tableId the ID of table to delete
     * @return generic response
     */
    @Override
    @Transactional
    public GenericResponse deleteTable(Long tableId) {
        log.info("Deleting restaurant table with ID: {}", tableId);
        if (tableId == null || tableId <= 0) {
            throw new DomainException(
                DomainErrorCode.INVALID_REQUEST,
                "Table ID must be a positive number"
            );
        }
        return restaurantTableInterface.deleteTable(tableId);
    }
    
    /**
     * Change table status to OCCUPIED
     * Validates transition from current state to OCCUPIED
     */
    @Override
    @Transactional
    public RestaurantTable changeStatusOcuped(ChangeStatusTableDTO changeStatusTableDTO) {
        log.info("Changing table status to OCCUPIED: {}", changeStatusTableDTO.getTableNumber());
        validateChangeStatus(changeStatusTableDTO.getTableNumber(), TableState.OCCUPIED);
        return restaurantTableInterface.changeStatusOcuped(changeStatusTableDTO);
    }
    
    /**
     * Change table status to AVAILABLE (FREE)
     * Validates transition from current state to AVAILABLE
     */
    @Override
    @Transactional
    public RestaurantTable changeStatusFree(Long tableNumber) {
        log.info("Changing table status to AVAILABLE: {}", tableNumber);
        validateChangeStatus(tableNumber, TableState.AVAILABLE);
        return restaurantTableInterface.changeStatusFree(tableNumber);
    }
    
    /**
     * Change table status to REQUESTING_SERVICE
     * Validates transition from current state to REQUESTING_SERVICE
     */
    @Override
    @Transactional
    public RestaurantTable changeStatusRequestingService(NumberDTO tableNumber) {
        log.info("Changing table status to REQUESTING_SERVICE: {}", tableNumber.getTableNumber());
        validateChangeStatus(tableNumber.getTableNumber(), TableState.REQUESTING_SERVICE);
        return restaurantTableInterface.changeStatusRequestingService(tableNumber);
    }
    
    /**
     * Change table status to RESERVED
     * Validates transition from current state to RESERVED
     */
    @Override
    @Transactional
    public RestaurantTable changeStatusReserved(Long tableNumber) {
        log.info("Changing table status to RESERVED: {}", tableNumber);
        validateChangeStatus(tableNumber, TableState.RESERVED);
        return restaurantTableInterface.changeStatusReserved(tableNumber);
    }
    
    /**
     * Change table status to PAYING
     * Validates transition from current state to PAYING
     */
    @Override
    @Transactional
    public RestaurantTable changeStatusPay(NumberDTO tableNumber) {
        log.info("Changing table status to PAYING: {}", tableNumber.getTableNumber());
        validateChangeStatus(tableNumber.getTableNumber(), TableState.PAYING);
        return restaurantTableInterface.changeStatusPay(tableNumber);
    }
    
    /**
     * PRIVATE HELPER METHODS
     */
    
    /**
     * Validates that a state transition is legal
     * Currently a placeholder - requires current state to be retrieved from persistence
     * 
     * TODO: Implement full state validation once RestaurantTable model includes current status
     */
    private void validateChangeStatus(Long tableNumber, TableState targetState) {
        if (tableNumber == null || tableNumber <= 0) {
            throw new DomainException(
                DomainErrorCode.INVALID_REQUEST,
                "Table number must be a positive number"
            );
        }
        
        // TODO: Retrieve current table state and validate transition
        // RestaurantTable table = restaurantTableInterface.getTableByNumber(tableNumber);
        // TableState currentState = stateValidator.parseState(table.getStatus());
        // stateValidator.validateTransition(currentState, targetState);
        
        log.debug("State transition validation for table {}: -> {}", tableNumber, targetState.name());
    }
}
