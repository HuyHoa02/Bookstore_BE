package com.chris.bookstore.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmailVerifyRequest {
    @NotBlank(message = "Verification code cannot be empty")
    @Size(min = 6, max = 6, message = "Verification code must be 6 characters long")
    private String verifyCode;

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public EmailVerifyRequest( String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public EmailVerifyRequest() {
    }
}
