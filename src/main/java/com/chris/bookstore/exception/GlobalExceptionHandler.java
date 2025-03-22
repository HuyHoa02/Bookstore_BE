package com.chris.bookstore.exception;

import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.enums.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
    public ResponseEntity<ApiResponse<String>> hanleRuntimeException(RuntimeException ex){
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
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = ex.getFieldErrors();

        ApiResponse<Object> res = new ApiResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).toList();
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
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
