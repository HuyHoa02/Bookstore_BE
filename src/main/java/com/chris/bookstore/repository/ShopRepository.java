package com.chris.bookstore.repository;

import com.chris.bookstore.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Shop getShopByOwnerId(Long id);
}
