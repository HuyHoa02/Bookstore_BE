package com.chris.bookstore.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "shops")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String shopName;

    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner; // The user who owns this shop (bidirectional)

    @OneToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address shopAddress; // Shop-specific address

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shop")
    private List<Book> books = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shop")
    private List<Order> orders = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "followed",
            joinColumns = @JoinColumn(name = "shop_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> followers = new HashSet<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopRating> ratings = new ArrayList<>();

    @Column(nullable = false)
    private boolean available = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public Address getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(Address shopAddress) {
        this.shopAddress = shopAddress;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public List<ShopRating> getRatings() {
        return ratings;
    }

    public void setRatings(List<ShopRating> ratings) {
        this.ratings = ratings;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}