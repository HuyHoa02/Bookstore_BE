package com.chris.bookstore.dto.request;


import jakarta.validation.constraints.*;

public class BookRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 50, message = "Title should not exceed 50 characters")
    private String title;

    @NotBlank(message = "Author cannot be empty")
    @Size(max = 50, message = "Author name should not exceed 50 characters")
    private String author;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 250, message = "Description should not exceed 250 characters")
    private String description;

    @Positive(message = "Price must be a positive value")
    private double price;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
