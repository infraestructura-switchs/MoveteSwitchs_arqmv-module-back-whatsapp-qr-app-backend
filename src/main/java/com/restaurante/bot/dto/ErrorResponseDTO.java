package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response DTO for consistent error handling across the API.
 * Used by GlobalExceptionHandler to provide uniform error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {
    
    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * HTTP status reason phrase (e.g., "Bad Request")
     */
    private String error;
    
    /**
     * Human-readable error message
     */
    private String message;
    
    /**
     * Internal error code from DomainErrorCode enum
     */
    private String errorCode;
    
    /**
     * API path that generated the error
     */
    private String path;
    
    /**
     * Field-level validation errors (only populated for validation errors)
     * Map of field name -> validation error message
     */
    private Map<String, String> validationErrors;
    
    /**
     * Additional details about the error (optional)
     */
    private String details;
}
