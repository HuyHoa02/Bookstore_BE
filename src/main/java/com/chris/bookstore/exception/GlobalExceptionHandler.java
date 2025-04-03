package com.chris.bookstore.exception;

import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.enums.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex){
        ApiResponse<String> apiResponse = new ApiResponse<String>();

        apiResponse.setStatusCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<String>> handleAppException(AppException ex){
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<String> apiResponse = new ApiResponse<String>();

        apiResponse.setStatusCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        if (errorCode == ErrorCode.TOKEN_EXPIRED) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed");

        StringBuilder errorMessage = new StringBuilder();
        BindingResult result = ex.getBindingResult();
        for (FieldError error : result.getFieldErrors()) {
            errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }

        response.setResult(errorMessage.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage(); // Interpolated message
            String messageTemplate = violation.getMessageTemplate(); // Raw messageTemplate

            errors.put(field, messageTemplate); // Store messageTemplate instead of interpolated message
        }


        ApiResponse<Map<String, String>> response = new ApiResponse<Map<String, String>>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setError("Validation failed");
        response.setMessage(errors);

        return ResponseEntity.badRequest().body(response);
    }
}
