package com.training.on_class.infrastructure.entrypoints.rest.exception;

import com.training.on_class.domain.exceptions.BusinessException;
import com.training.on_class.infrastructure.entrypoints.rest.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDomainException(BusinessException ex) {
        return Mono.just(ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(WebExchangeBindException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
          .map(error -> error.getField() + ": " + error.getDefaultMessage())
          .collect(Collectors.joining(", "));

        return Mono.just(ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value())));
    }
}