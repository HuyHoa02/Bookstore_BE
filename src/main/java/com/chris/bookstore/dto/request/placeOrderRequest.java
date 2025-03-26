package com.chris.bookstore.dto.request;


public class placeOrderRequest {
    private Long userId;
    private Long addressId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long address) {
        this.addressId = address;
    }
}
