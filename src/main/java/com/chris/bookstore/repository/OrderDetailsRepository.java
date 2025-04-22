package com.chris.bookstore.repository;

import com.chris.bookstore.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    @Query("""
    SELECT COUNT(od) > 0 FROM OrderDetails od
    WHERE od.order.user.id = :userId
      AND od.book.id = :bookId
      AND od.order.status = 'SHIPPED'
    """)
    boolean hasUserBoughtBook(@Param("userId") Long userId,
                              @Param("bookId") Long bookId);

}
