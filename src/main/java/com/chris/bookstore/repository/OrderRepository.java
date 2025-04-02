package com.chris.bookstore.repository;

import com.chris.bookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrdersByShopId(Long shopId);
    Order getOrderByIdAndShopId(Long orderId, Long shopId);
}
