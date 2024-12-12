package com.example.data_processing_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class PriceFetchingException extends RuntimeException {
    public PriceFetchingException(String message) {
        super(message);
    }
}
