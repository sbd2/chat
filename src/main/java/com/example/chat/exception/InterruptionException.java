package com.example.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CHECKPOINT)
public class InterruptionException extends RuntimeException {
    public InterruptionException() {
        super();
    }
    public InterruptionException(String message, Throwable cause) {
        super(message, cause);
    }
    public InterruptionException(String message) {
        super(message);
    }
    public InterruptionException(Throwable cause) {
        super(cause);
    }
}
