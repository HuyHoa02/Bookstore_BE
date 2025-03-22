package com.chris.bookstore.enums;

public enum OrderStatus {
    PENDING(0,"PENDING"),
    CONFIRMED(1, "CONFIRMED"),
    SHIPPED(2,"SHIPPED"),
    DELIVERED(3,"DELIVERED"),
    CANCELLED(4,"CANCELLED");

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    OrderStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
