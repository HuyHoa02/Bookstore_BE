package com.chris.bookstore.enums;

import com.chris.bookstore.exception.AppException;

public enum OrderStatus {
    PENDING(0,"PENDING"),
    CONFIRMED(1, "CONFIRMED"),
    DELIVERED(2,"DELIVERED"),
    SHIPPED(3,"SHIPPED"),
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
    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
    }
}
