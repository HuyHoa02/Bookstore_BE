package com.chris.bookstore.repository;

import com.chris.bookstore.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    CartItems findByCartIdAndBookId(Long cartId, Long bookId);
}
