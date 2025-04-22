package com.chris.bookstore.dto.response;

import com.chris.bookstore.entity.CartItems;

import java.util.ArrayList;
import java.util.List;

public class CartItemsResponse {
    private long shopId;
    private String shopName;
    private List<Item> items = new ArrayList<>();

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private long bookId;
        private String title;
        private double price;
        private int quantity;
        private double totalAmount;

        public Item() {}

        public Item(CartItems item) {
            this.bookId = item.getBook().getId();
            this.title = item.getBook().getTitle();
            this.price = item.getUnitPrice();
            this.quantity = item.getQuantity();
            this.totalAmount = item.getQuantity() * item.getUnitPrice();
        }

        // ✅ Getters và Setters
        public long getBookId() {
            return bookId;
        }

        public void setBookId(long bookId) {
            this.bookId = bookId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}
