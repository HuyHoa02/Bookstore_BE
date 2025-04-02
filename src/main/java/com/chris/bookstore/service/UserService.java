package com.chris.bookstore.service;

import com.chris.bookstore.entity.Shop;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.ShopRepository;
import com.chris.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    public UserService(UserRepository userRepository,
                       ShopRepository shopRepository){
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    public User getUserByUsername(String username){
        return this.userRepository.findByUsername(username);
    }

    public void updateUserToken(String token, String username)
    {
        User user = this.userRepository.findByUsername(username);
        if(user != null)
        {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndUsername(String token, String username)
    {
        User user = this.userRepository.findByRefreshTokenAndUsername(token,username);
        if(user ==null)
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        return user;
    }

    public User getCurrentUser() {
        // Lấy Authentication từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Lấy thông tin người dùng từ Authentication
            String username = authentication.getName();
            return userRepository.getUserByUsername(username);
        }
        return null; // Hoặc throw exception nếu không có người dùng đăng nhập
    }

    @Transactional
    public void followShop(Long shopId) {
        User currentUser = getCurrentUser(); // Assume method to get the logged-in user
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_EXISTED));

        // Prevent user from following their own shop
        if (shop.getOwner().equals(currentUser)) {
            throw new AppException(ErrorCode.CANNOT_FOLLOW_OWN_SHOP);
        }

        // Prevent duplicate following
        if (currentUser.getFollowedShop().contains(shop)) {
            throw new AppException(ErrorCode.ALREADY_FOLLOWED);
        }

        // Add shop to user's followed list
        currentUser.getFollowedShop().add(shop);
        shop.getFollowers().add(currentUser);

        userRepository.save(currentUser);
        shopRepository.save(shop);
    }
}
