package edu.cit.estrera.wearisit.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {

        ApiError error = new ApiError();
        error.setCode("BAD_REQUEST");
        error.setMessage(ex.getMessage());
        error.setDetails(null);

        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }
}