package com.chris.bookstore.repository;

import com.chris.bookstore.entity.Shop;
import com.chris.bookstore.entity.ShopRating;
import com.chris.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRatingRepository extends JpaRepository<ShopRating, Long> {
    boolean existsByUserAndShop(User user, Shop shop);

    @Query("SELECT COALESCE(AVG(s.rating), 0) FROM ShopRating s WHERE s.shop.id = :shopId")
    Double getAverageRatingByShopId(@Param("shopId") Long shopId);
}
