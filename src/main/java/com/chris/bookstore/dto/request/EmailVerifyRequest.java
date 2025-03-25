package com.chris.bookstore.dto.request;


public class EmailVerifyRequest {
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
