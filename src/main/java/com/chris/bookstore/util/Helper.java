package com.chris.bookstore.util;

import com.chris.bookstore.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class Helper {
    public String generateTempPwd(int length) {
        String numbers = "012345678";
        char otp[] = new char[length];
        Random getOtpNum = new Random();
        for (int i = 0; i < length; i++) {
            otp[i] = numbers.charAt(getOtpNum.nextInt(numbers.length()));
        }
        String optCode = "";
        for (int i = 0; i < otp.length; i++) {
            optCode += otp[i];
        }
        return optCode;
    }

    public <T> ApiResponse<T> buildResponse(HttpStatus status, String message, T result) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatusCode(status.value());
        response.setMessage(message);
        response.setResult(result);
        return response;
    }
}
