package com.chris.bookstore.enums;

public enum ErrorCode {
    CATEGORY_NOT_EXISTED(1000,"Category is not existed in database"),
    CATEGORY_EXISTED(1001,"Category is not existed in database"),
    BOOK_NOT_EXISTED(1002,"Book is not existed in database"),
    CART_NOT_EXISTED(1003,"Cart is not existed in database"),
    ITEM_NOT_EXISTED(1004,"This item is not existed in database"),
    CART_EMPTY(1005,"Can not place an order because yout cart is empty"),
    ORDER_NOT_EXISTED(1006,"Order is not existed in database"),
    STATUS_OPTION_INVALID(1007,"Order status option is not valid"),
    USER_NOT_EXISTED(1008,"User is not existed in database"),
    ADDRESS_NOT_EXISTED(1008,"Address is not existed in database"),
    USER_EXISTED(1008,"User is t existed in database"),
    INVALID_REFRESH_TOKEN(1009,"Can not found refresh token in cookie"),
    TOKEN_EXPIRED(1010,"Token is expired"),
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized exception occur")
    ;

    private int code;
    private String message;

    ErrorCode(int errorCode, String message) {
        this.code = errorCode;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
