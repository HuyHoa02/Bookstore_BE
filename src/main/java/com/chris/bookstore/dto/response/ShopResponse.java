package com.chris.bookstore.dto.response;


import com.chris.bookstore.entity.Address;
import com.chris.bookstore.entity.User;

import java.util.Set;

public class ShopResponse {
    private String shopName;
    private Address shopAddress;
    private Long shopFollowers;
    private double shopRating;
    private Long shopOwnerId;

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

    public Long getShopFollowers() {
        return shopFollowers;
    }

    public void setShopFollowers(Long shopFollowers) {
        this.shopFollowers = shopFollowers;
    }

    public double getShopRating() {
        return shopRating;
    }

    public void setShopRating(double shopRating) {
        this.shopRating = shopRating;
    }

    public Long getShopOwnerId() {
        return shopOwnerId;
    }

    public void setShopOwnerId(Long shopOwnerId) {
        this.shopOwnerId = shopOwnerId;
    }
}
