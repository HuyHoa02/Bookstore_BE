package com.chris.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateBookInfoRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 50, message = "Title should not exceed 50 characters")
    private String title;

    @NotBlank(message = "Author cannot be empty")
    @Size(max = 50, message = "Author name should not exceed 50 characters")
    private String author;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 250, message = "Description should not exceed 250 characters")
    private String description;

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
}
