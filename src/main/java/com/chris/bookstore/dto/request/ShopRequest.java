package com.chris.bookstore.dto.request;


import com.chris.bookstore.entity.Address;

public class ShopRequest {
    private String shopName;
    private Address shopAddress;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Address getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(Address shopAddress) {
        this.shopAddress = shopAddress;
    }
}
