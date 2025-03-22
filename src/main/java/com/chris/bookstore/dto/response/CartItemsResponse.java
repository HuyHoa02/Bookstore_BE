package com.chris.bookstore.dto.response;


import com.chris.bookstore.entity.CartItems;

public class CartItemsResponse {
    private String title;
    private double price;
    private int quantity;
    private double totalAmount;

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

    public CartItemsResponse(CartItems item) {
        this.title = item.getBook().getTitle();
        this.price = item.getUnitPrice();
        this.quantity = item.getQuantity();
        this.totalAmount = item.getUnitPrice()*item.getQuantity();
    }
}
