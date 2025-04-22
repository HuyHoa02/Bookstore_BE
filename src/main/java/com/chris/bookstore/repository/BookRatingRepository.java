package com.chris.bookstore.repository;

import com.chris.bookstore.entity.Book;
import com.chris.bookstore.entity.BookRating;
import com.chris.bookstore.entity.Shop;
import com.chris.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRatingRepository extends JpaRepository<BookRating, Long> {
    boolean existsByUserAndBook(User user, Book book);

    Optional<BookRating> findByUserIdAndBookId(Long userId,Long bookId);

    @Query("SELECT COALESCE(AVG(b.rating), 0) FROM BookRating b WHERE b.book.id = :bookId")
    Double getAverageRatingByShopId(@Param("bookId") Long bookId);
}
