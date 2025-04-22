package com.chris.bookstore.dto.request;

import java.util.List;

public class PlaceOrderRequest {
    private String shippingAddress;
    private String note;
    private List<Long> cartItemIds; // 👈 IDs of CartItems to order

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Long> getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(List<Long> cartItemIds) {
        this.cartItemIds = cartItemIds;
    }
}
