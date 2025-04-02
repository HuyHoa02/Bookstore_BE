package com.chris.bookstore.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "shop_id"})
})
@Entity
public class ShopRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;
    private String review;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Ensure unique rating per user & shop
    @Column(unique = true)
    private String userShopKey;

    @PrePersist
    public void generateUniqueKey() {
        this.userShopKey = user.getId() + "_" + shop.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserShopKey() {
        return userShopKey;
    }

    public void setUserShopKey(String userShopKey) {
        this.userShopKey = userShopKey;
    }
}
