package com.restaurante.bot.config.exception;

import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.dto.ErrorResponseDTO;
import com.restaurante.bot.exception.CustomErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for centralized exception management.
 * Removes try-catch blocks from controllers and provides consistent error responses.
 *
 * This is a critical layer in clean architecture that ensures:
 * - Controllers only handle HTTP concerns
 * - Business logic exceptions are handled in one place
 * - Consistent error response format across all endpoints
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * Handle DomainException - business logic exceptions
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponseDTO> handleDomainException(
            DomainException domainException,
            WebRequest request) {
        
        log.error("Domain exception occurred: {}", domainException.getMessage(), domainException);
        
        HttpStatus status = mapDomainErrorCodeToHttpStatus(domainException.getCode());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(domainException.getMessage())
                .errorCode(domainException.getCode().name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException validationException,
            WebRequest request) {
        
        log.warn("Validation error occurred: {}", validationException.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        validationException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .errorCode(DomainErrorCode.INVALID_REQUEST.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .validationErrors(validationErrors)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle CustomErrorException - application-specific errors with custom HTTP status
     */
    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomErrorException(
            CustomErrorException customException,
            WebRequest request) {
        
        HttpStatus status = customException.getStatus() != null ? 
            customException.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        
        log.warn("Custom error occurred [{}]: {}", status.value(), customException.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(customException.getMessage())
                .errorCode(status.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, status);
    }

    /**     * Handle JSON parsing errors and invalid enum values
     * Returns 400 Bad Request with user-friendly message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {
        
        log.warn("Invalid request format: {}", ex.getMessage());
        
        String message = "Formato de solicitud inválido. Verifique los datos enviados.";
        
        // Check for enum deserialization errors
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String cause = ex.getCause().getMessage();
            if (cause.contains("not one of the values accepted for Enum")) {
                message = "Valor inválido. Verifique los parámetros de la solicitud.";
            }
        }
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .errorCode(DomainErrorCode.INVALID_REQUEST.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle generic exceptions - catch-all for unexpected errors
     * Logs detailed stack trace but returns generic message to client for security
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception exception,
            WebRequest request) {
        
        log.error("Unexpected exception occurred - Technical details:", exception);
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Ocurrió un error interno del servidor. Intente nuevamente más tarde.")
                .errorCode(DomainErrorCode.INTERNAL_ERROR.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle database connectivity / datasource failures (Hikari/SQL exceptions)
     * Logs detailed error for debugging but returns generic message to client
     */
    @ExceptionHandler({DataAccessResourceFailureException.class, SQLTransientConnectionException.class, SQLException.class})
    public ResponseEntity<ErrorResponseDTO> handleDatabaseException(
            Exception exception,
            WebRequest request) {

        log.error("Database connectivity error - Technical details: {}", exception.getMessage(), exception);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                .message("El servicio no está disponible en este momento. Intente nuevamente más tarde.")
                .errorCode(DomainErrorCode.SERVICE_UNAVAILABLE.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Improve message for bean wiring errors where multiple candidates exist
     * Logs technical details but returns generic message to client
     */
    @ExceptionHandler(NoUniqueBeanDefinitionException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoUniqueBean(NoUniqueBeanDefinitionException ex, WebRequest request) {
        log.error("Bean wiring configuration error - Technical details: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Error de configuración del servidor. Contacte al administrador.")
                .errorCode(DomainErrorCode.INTERNAL_ERROR.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException exception,
            WebRequest request) {
        
        log.warn("Illegal argument exception: {}", exception.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .errorCode(DomainErrorCode.INVALID_REQUEST.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParam(MissingServletRequestParameterException ex, WebRequest request) {
        log.warn("Missing request parameter: {}", ex.getParameterName());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Falta el parametro obligatorio: " + ex.getParameterName())
                .errorCode(DomainErrorCode.INVALID_REQUEST.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.warn("Type mismatch for parameter {}: expected {}", ex.getName(), ex.getRequiredType());

        String typeName = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "";
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("El parametro '" + ex.getName() + "' debe ser de tipo " + typeName)
                .errorCode(DomainErrorCode.INVALID_REQUEST.name())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Map DomainErrorCode to HttpStatus
     */
    private HttpStatus mapDomainErrorCodeToHttpStatus(DomainErrorCode code) {
        return switch (code) {
            case INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case CONFLICT -> HttpStatus.CONFLICT;
            case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
