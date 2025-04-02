package com.chris.bookstore.dto.response;


import com.chris.bookstore.entity.Book;
import com.chris.bookstore.entity.Order;
import com.chris.bookstore.entity.OrderDetails;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.OrderStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class OrderResponse {
    private Long id;
    private UserOrderd userOrderd;
    private double totalAmount;
    private String note;
    private OrderStatus status;
    private String shippingAddress;
    private List<Details> details = new ArrayList<>();

    private class UserOrderd {
        private Long id;
        private String username;
        private String fullname;
        private String email;

        public UserOrderd(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullName();
            this.email = user.getEmail();
        }
    }

    private class Details{
        private Long bookId;
        private String bookTitle;
        private Integer quantity;
        private double unitPrice;

        public Details(OrderDetails orderDetails) {
            this.bookId = orderDetails.getBook().getId();
            this.bookTitle = orderDetails.getBook().getTitle();
            this.quantity = orderDetails.getQuantity();
            this.unitPrice = orderDetails.getUnitPrice();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserOrderd getUserOrderd() {
        return userOrderd;
    }

    public void setUserOrderd(UserOrderd userOrderd) {
        this.userOrderd = userOrderd;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<Details> getDetails() {
        return details;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userOrderd = new UserOrderd(order.getUser());
        this.totalAmount = order.getTotalAmount();
        this.note = order.getNote();
        this.status = order.getStatus();
        this.shippingAddress = order.getShippingAddress();
        this.details = order.getOrderDetails().stream().map(Details::new).toList();
    }
}
