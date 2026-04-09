package com.restaurante.bot.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLTransientConnectionException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomExceptionHandler {
    private final ErrorMessageService messageService;
    /**
     * Exception handler for GenericException.
     * <p>
     * Handles the exception and returns an appropriate response entity.
     *
     * @param ex       the AuthorizationException instance
     * @param request the WebRequest object
     * @return the ResponseEntity containing the error response
     */

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<Object> handleGenericException(GenericException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        // Log full details for internal debugging
        log.warn("[{}] Business exception: {}", traceId, ex.getMessage());
        String msg = sanitizeMessage(ex.getMessage());
        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
                ErrorCode.INVALID_REQUEST.toString(),
                msg,
                traceId);
        HttpStatus status = ex.getStatus() == null ? HttpStatus.BAD_REQUEST : ex.getStatus();
        return ResponseEntity.status(status).body(resp);
    }

    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<Object> handleCustomErrorException(CustomErrorException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] CustomErrorException: status={} messageKey={} message={}", traceId, ex.getStatus(), ex.getMessageKey(), ex.getMessage());
        String msg = ex.getMessageKey() != null ? messageService.getMessage(ex.getMessageKey()) : sanitizeMessage(ex.getMessage());
        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
                ErrorCode.INVALID_REQUEST.toString(),
                msg,
                traceId);
        HttpStatus status = ex.getStatus() == null ? HttpStatus.BAD_REQUEST : ex.getStatus();
        return ResponseEntity.status(status).body(resp);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> handleDomainException(DomainException ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] Domain exception: code={} messageKey={}", traceId, ex.getCode(), ex.getMessageKey());
        String msg;
        if (ex.getMessageKey() != null) {
            msg = messageService.getMessage(ex.getMessageKey(), ex.getArgs());
        } else {
            msg = sanitizeMessage(ex.getMessage());
        }

        ErrorCode publicCode = mapDomainToErrorCode(ex.getCode());
        HttpStatus status = mapDomainToHttpStatus(ex.getCode());

        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
                publicCode.toString(),
                msg,
                traceId);
        return ResponseEntity.status(status).body(resp);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex,
                                                                                WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.info("[{}] Missing parameter: {}", traceId, ex.getParameterName());
        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
            ErrorCode.INVALID_REQUEST.toString(),
            messageService.getMessage("missing.param", ex.getParameterName()),
            traceId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                            WebRequest request) {
        String requiredType = ex.getRequiredType() == null ? "valor valido" : ex.getRequiredType().getSimpleName();
        String traceId = UUID.randomUUID().toString();
        log.info("[{}] Type mismatch for param {}: expected {}", traceId, ex.getName(), requiredType);
        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
            ErrorCode.INVALID_REQUEST.toString(),
            messageService.getMessage("type.mismatch", ex.getName(), requiredType),
            traceId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        Throwable cause = ex.getMostSpecificCause();
        String traceId = UUID.randomUUID().toString();
        String message = messageService.getMessage("json.invalid");
        if (cause instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException upe = (UnrecognizedPropertyException) cause;
            String prop = upe.getPropertyName();
            message = messageService.getMessage("json.unknown.field", prop);
            log.info("[{}] Unknown JSON field: {}", traceId, prop);
        } else {
            log.info("[{}] JSON parse error: {}", traceId, ex.getMessage());
        }
        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
                ErrorCode.INVALID_REQUEST.toString(),
                message,
                traceId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler({
            CannotCreateTransactionException.class,
            CannotGetJdbcConnectionException.class,
            DataAccessResourceFailureException.class,
            JDBCConnectionException.class,
            SQLTransientConnectionException.class
    })
    public ResponseEntity<Object> handleDatabaseUnavailable(Exception ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] Database unavailable: {}", traceId, ex.getMessage(), ex);
        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
            ErrorCode.SERVICE_UNAVAILABLE.toString(),
            messageService.getMessage("service.unavailable.db"),
            traceId);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(resp);
    }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String traceId = UUID.randomUUID().toString();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        log.info("[{}] Validation errors: {}", traceId, errors);
        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
            ErrorCode.INVALID_REQUEST.toString(),
            messageService.getMessage("validation.error"),
            traceId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> handleAllUncaught(Exception ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("[{}] Unexpected error: {}", traceId, ex.getMessage(), ex);
        ErrorResponse resp = new ErrorResponse(LocalDateTime.now(),
            ErrorCode.INTERNAL_ERROR.toString(),
            messageService.getMessage("unexpected.error", traceId),
            traceId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }

        private String sanitizeMessage(String message) {
        if (message == null) return "";
        // Simple sanitization: avoid returning stack traces or SQL messages
        return message.replaceAll("(?i)exception", "").trim();
        }

    private ErrorCode mapDomainToErrorCode(DomainErrorCode code) {
        switch (code) {
            case INVALID_REQUEST:
                return ErrorCode.INVALID_REQUEST;
            case NOT_FOUND:
                return ErrorCode.NOT_FOUND;
            case UNAUTHORIZED:
                return ErrorCode.UNAUTHORIZED;
            case FORBIDDEN:
                return ErrorCode.FORBIDDEN;
            case CONFLICT:
                return ErrorCode.CONFLICT;
            case SERVICE_UNAVAILABLE:
                return ErrorCode.SERVICE_UNAVAILABLE;
            default:
                return ErrorCode.INTERNAL_ERROR;
        }
    }

    private HttpStatus mapDomainToHttpStatus(DomainErrorCode code) {
        switch (code) {
            case INVALID_REQUEST:
                return HttpStatus.BAD_REQUEST;
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case UNAUTHORIZED:
                return HttpStatus.UNAUTHORIZED;
            case FORBIDDEN:
                return HttpStatus.FORBIDDEN;
            case CONFLICT:
                return HttpStatus.CONFLICT;
            case SERVICE_UNAVAILABLE:
                return HttpStatus.SERVICE_UNAVAILABLE;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}