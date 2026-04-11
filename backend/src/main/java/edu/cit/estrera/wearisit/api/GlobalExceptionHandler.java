package edu.cit.estrera.wearisit.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException ex){

        ApiError error = new ApiError(
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getMessage(),
                ex.getDetails()
        );

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex){

        ApiError error = new ApiError(
                ErrorCode.SYSTEM_001.getCode(),
                ErrorCode.SYSTEM_001.getMessage(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(error));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiError error = new ApiError("VAL-001", "Validation failed", msg);
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

}