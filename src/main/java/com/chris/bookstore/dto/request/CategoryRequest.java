package com.chris.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100, message = "Category name should not exceed 100 characters")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 250, message = "Description should not exceed 250 characters")
    private String description;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
