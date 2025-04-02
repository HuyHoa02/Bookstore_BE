package com.chris.bookstore.dto.request;


import com.chris.bookstore.entity.Address;

public class ShopRequest {
    private String shopName;
    private Long addressId;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
