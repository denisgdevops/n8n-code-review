package com.bank.balance.api;

import com.bank.balance.service.AccountNotFoundException;
import com.bank.balance.service.InvalidAdditionalDataException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class, InvalidAdditionalDataException.class})
    ResponseEntity<ErrorResponse> validation(Exception exception, HttpServletRequest request) {
        LOG.warn("Validation rejected for operation={}", request.getRequestURI());
        return failure(HttpStatus.BAD_REQUEST, "VAL001", "Request validation failed",
                "VAL001", "One or more request values are invalid");
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(AccountNotFoundException exception) {
        return failure(HttpStatus.BAD_REQUEST, "ACC001", "Account was not found",
                "ACC001", "The requested synthetic account does not exist");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception exception) {
        LOG.error("Unexpected balance enquiry failure", exception);
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, "SYS001", "Internal server error",
                "SYS001", "The request could not be completed");
    }

    private ResponseEntity<ErrorResponse> failure(HttpStatus httpStatus, String messageCode,
            String messageDescription, String errorCode, String errorDescription) {
        String traceID = MDC.get("TraceID");
        ErrorResponse response = new ErrorResponse("1", "Failed", "UNAVAILABLE", traceID,
                messageCode, messageDescription, List.of(new ErrorInfo(errorCode, errorDescription)));
        return ResponseEntity.status(httpStatus).body(response);
    }
}
