package com.chris.bookstore.enums;

public enum ErrorCode {
    CATEGORY_NOT_EXISTED(1000,"Category is not existed in database"),
    CATEGORY_EXISTED(1001,"Category existed in database"),
    BOOK_NOT_EXISTED(1002,"Book is not existed in database"),
    CART_NOT_EXISTED(1003,"Cart is not existed in database"),
    ITEM_NOT_EXISTED(1004,"This item is not existed in database"),
    CART_EMPTY(1005,"Can not place an order because yout cart is empty"),
    ORDER_NOT_EXISTED(1006,"Order is not existed in database"),
    STATUS_OPTION_INVALID(1007,"Order status option is not valid"),
    USER_NOT_EXISTED(1008,"User is not existed in database"),
    ADDRESS_NOT_EXISTED(1008,"Address is not existed in database"),
    USER_EXISTED(1008,"User is existed in database"),
    INVALID_REFRESH_TOKEN(1009,"Can not found refresh token in cookie"),
    TOKEN_EXPIRED(1010,"Token is expired"),
    VERIFY_EMAIL_FAILED(1011,"Email validation code is invalid"),
    SHOP_EXISTED(1012,"Current user's shop existed in database"),
    SHOP_NOT_EXISTED(1013,"Current user's shop not existed in database"),
    INSUFFICIENT_STOCK(1014,"Your current products is out of stock"),
    INVALID_ADDRESS(1015,"Current address is in valid through operation"),
    ALREADY_RATED(1016,"User already rated this shop before"),
    INVALID_RATE(1017,"User cant not rate their own shop"),
    CANNOT_FOLLOW_OWN_SHOP(1018,"User cant not follow their own shop"),
    ALREADY_FOLLOWED(1019,"User already followed this shop before"),
    INVALID_CART_SELECTION(1020, "No cart items selected for ordering."),
    BOOK_NOT_PURCHASED(1021, "User can not rate book that their never bought"),
    BOOK_ALREADY_RATED(1022, "This book had been rate by user."),
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
