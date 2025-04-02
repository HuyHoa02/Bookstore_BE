package com.chris.bookstore.service;

import com.chris.bookstore.entity.Shop;
import com.chris.bookstore.entity.ShopRating;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.ShopRatingRepository;
import com.chris.bookstore.repository.ShopRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;
@Service
public class ShopRatingService {
    private ShopRatingRepository shopRatingRepository;
    private UserService userService;
    private ShopRepository shopRepository;

    public ShopRatingService(
            ShopRatingRepository shopRatingRepository,
            UserService userService,
            ShopRepository shopRepository
    ){
        this.shopRatingRepository = shopRatingRepository;
        this.userService = userService;
        this.shopRepository = shopRepository;
    }

    @Transactional
    public void rateShop(Long shopId, int rating, String review) {
        User currentUser = userService.getCurrentUser();
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_EXISTED));

        if (currentUser.getShop() == shop)
            throw new AppException(ErrorCode.INVALID_RATE);

        if (shopRatingRepository.existsByUserAndShop(currentUser, shop))
            throw new AppException(ErrorCode.ALREADY_RATED);

        ShopRating newRating = new ShopRating();
        newRating.setUser(currentUser);
        newRating.setShop(shop);
        newRating.setRating(rating);
        newRating.setReview(review);

        shopRatingRepository.save(newRating);
    }

    public double getShopAverageRating(Long shopId) {
        return shopRatingRepository.getAverageRatingByShopId(shopId);
    }
}
