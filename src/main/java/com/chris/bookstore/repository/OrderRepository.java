package com.chris.bookstore.repository;

import com.chris.bookstore.entity.Order;
import com.chris.bookstore.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrdersByShopId(Long shopId);
    @Query("SELECT o FROM Order o WHERE o.shop.id = :shopId AND o.status = :status")
    List<Order> findByShopIdAndStatus(@Param("shopId") Long shopId,
                                      @Param("status") OrderStatus status);

    Order getOrderByIdAndShopId(Long orderId, Long shopId);
}
