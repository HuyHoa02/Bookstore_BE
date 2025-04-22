package com.chris.bookstore.entity;

import jakarta.persistence.*;

@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_id"})
})
@Entity
public class BookRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;
    private String review;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Ensure unique rating per user & shop
    @Column(unique = true)
    private String userBookKey;

    @PrePersist
    public void generateUniqueKey() {
        this.userBookKey = user.getId() + "_" + book.getId();
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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserBookKey() {
        return userBookKey;
    }

    public void setUserBookKey(String userBookKey) {
        this.userBookKey = userBookKey;
    }
}
