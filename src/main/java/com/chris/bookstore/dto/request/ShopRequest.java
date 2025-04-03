package com.chris.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ShopRequest {
    @NotBlank(message = "Shop name cannot be empty")
    private String shopName;

    @Positive(message = "Address ID must be a positive number")
    @NotNull(message = "Address ID cannot be null")
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
