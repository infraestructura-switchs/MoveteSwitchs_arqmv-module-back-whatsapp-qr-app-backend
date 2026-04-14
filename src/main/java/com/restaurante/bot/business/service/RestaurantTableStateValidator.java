package com.restaurante.bot.business.service;

import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

/**
 * Restaurant Table State Validator - validates legal state transitions for restaurant tables.
 * 
 * This component encapsulates the state machine logic for table status management.
 * It ensures that tables can only transition between valid states, preventing invalid
 * states and business logic violations.
 * 
 * State Machine Design:
 * States:
 * - AVAILABLE: Table is ready for customers
 * - OCCUPIED: Table has customers dining
 * - RESERVED: Table is reserved for future use
 * - REQUESTING_SERVICE: Table has customers requesting service
 * - PAYING: Table customers are settling payment
 * 
 * Valid Transitions:
 * AVAILABLE → OCCUPIED, RESERVED
 * OCCUPIED → REQUESTING_SERVICE, PAYING
 * REQUESTING_SERVICE → OCCUPIED, PAYING
 * RESERVED → AVAILABLE, OCCUPIED
 * PAYING → AVAILABLE
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RestaurantTableStateValidator {
    
    /**
     * Enum for restaurant table states
     */
    public enum TableState {
        AVAILABLE,
        OCCUPIED,
        RESERVED,
        REQUESTING_SERVICE,
        PAYING,
        CLEANING
    }
    
    /**
     * Validates if a state transition is legal
     * 
     * @param currentState the current state of the table
     * @param targetState the desired new state
     * @throws DomainException if transition is not allowed
     */
    public void validateTransition(TableState currentState, TableState targetState) {
        if (!isValidTransition(currentState, targetState)) {
            String errorMessage = String.format(
                "Invalid state transition: %s → %s is not allowed",
                currentState.name(), targetState.name()
            );
            log.warn(errorMessage);
            throw new DomainException(
                DomainErrorCode.INVALID_REQUEST,
                errorMessage
            );
        }
        log.debug("Valid transition: {} → {}", currentState.name(), targetState.name());
    }
    
    /**
     * Checks if a state transition is legal without throwing exception
     */
    public boolean isValidTransition(TableState currentState, TableState targetState) {
        Set<TableState> validNextStates = getValidTransitions(currentState);
        return validNextStates.contains(targetState);
    }
    
    /**
     * Returns valid next states for a given current state
     * This is the heart of the state machine definition
     */
    public Set<TableState> getValidTransitions(TableState currentState) {
        return switch (currentState) {
            case AVAILABLE -> EnumSet.of(TableState.OCCUPIED, TableState.RESERVED, TableState.CLEANING);
            case OCCUPIED -> EnumSet.of(TableState.REQUESTING_SERVICE, TableState.PAYING, TableState.AVAILABLE);
            case RESERVED -> EnumSet.of(TableState.AVAILABLE, TableState.OCCUPIED, TableState.CLEANING);
            case REQUESTING_SERVICE -> EnumSet.of(TableState.OCCUPIED, TableState.PAYING, TableState.AVAILABLE);
            case PAYING -> EnumSet.of(TableState.AVAILABLE, TableState.CLEANING);
            case CLEANING -> EnumSet.of(TableState.AVAILABLE);
        };
    }
    
    /**
     * Convert string to TableState enum with validation
     */
    public TableState parseState(String stateString) {
        try {
            return TableState.valueOf(stateString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainException(
                DomainErrorCode.INVALID_REQUEST,
                "Invalid table state: " + stateString
            );
        }
    }
    
    /**
     * Get human-readable description of valid transitions
     */
    public String getTransitionDescription(TableState currentState) {
        Set<TableState> validStates = getValidTransitions(currentState);
        return String.format(
            "From %s, table can transition to: %s",
            currentState.name(),
            validStates.stream().map(TableState::name).reduce((a, b) -> a + ", " + b).orElse("NONE")
        );
    }
}
