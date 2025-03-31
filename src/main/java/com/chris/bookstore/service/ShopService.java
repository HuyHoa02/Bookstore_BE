package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.ShopRequest;
import com.chris.bookstore.dto.response.ShopResponse;
import com.chris.bookstore.entity.Shop;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.ShopRepository;
import org.springframework.stereotype.Service;

@Service
public class ShopService {
    private ShopRepository shopRepository;
    private UserService userService;

    public ShopService(ShopRepository shopRepository,
                       UserService userService)
    {
        this.shopRepository = shopRepository;
        this.userService = userService;
    }

    public ShopResponse createShop(ShopRequest request)
    {
        User currentUser = this.userService.getCurrentUser();
        if(currentUser == null)
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        if(currentUser.getShop() != null)
            throw new AppException(ErrorCode.SHOP_EXISTED);

        Shop newShop = new Shop();
        newShop.setShopName(request.getShopName());
        newShop.setShopAddress(request.getShopAddress());
        newShop.setOwner(currentUser);

        shopRepository.save(newShop);

        ShopResponse res = new ShopResponse();
        res.setShopName(newShop.getShopName());
        res.setShopAddress(newShop.getShopAddress());
        res.setShopFollowers((long) newShop.getFollowers().size());
        res.setShopRating(newShop.getRating());
        res.setShopOwnerId(newShop.getOwner().getId());

        return res;
    }

    public void updateShop(ShopRequest request)
    {
        User currentUser = this.userService.getCurrentUser();
        if(currentUser == null)
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        if(currentUser.getShop() == null)
            throw new AppException(ErrorCode.SHOP_NOT_EXISTED);

        Shop currentShop = this.shopRepository.getShopByOwnerId(currentUser.getId());
        currentShop.setShopName(request.getShopName());
        currentShop.setShopAddress(request.getShopAddress());

        shopRepository.save(currentShop);
    }

    public void updateShopAvai()
    {
        User currentUser = this.userService.getCurrentUser();
        if(currentUser == null)
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        if(currentUser.getShop() == null)
            throw new AppException(ErrorCode.SHOP_NOT_EXISTED);

        Shop currentShop = this.shopRepository.getShopByOwnerId(currentUser.getId());
        currentShop.setAvailable(!currentShop.isAvailable());

        shopRepository.save(currentShop);
    }
}
