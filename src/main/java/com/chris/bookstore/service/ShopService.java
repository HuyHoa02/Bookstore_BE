package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.ShopRequest;
import com.chris.bookstore.dto.response.ShopResponse;
import com.chris.bookstore.entity.Address;
import com.chris.bookstore.entity.Shop;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.enums.Privilege;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.AddressRepository;
import com.chris.bookstore.repository.ShopRepository;
import com.chris.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ShopService {
    private final ShopRepository shopRepository;
    private final UserService userService;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public ShopService(ShopRepository shopRepository,
                       UserService userService,
                       AddressRepository addressRepository,
                       UserRepository userRepository)
    {
        this.shopRepository = shopRepository;
        this.userService = userService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public ShopResponse createShop(ShopRequest request) {
        User currentUser = this.userService.getCurrentUser();
        if (currentUser.getShop() != null)
            throw new AppException(ErrorCode.SHOP_EXISTED);

        Address currentAddress = addressRepository.findByUserIdAndId(currentUser.getId(), request.getAddressId());
        if (!currentUser.getAddresses().contains(currentAddress))
            throw new AppException(ErrorCode.INVALID_ADDRESS);

        // Create shop and link it to user
        Shop newShop = new Shop();
        newShop.setShopName(request.getShopName());
        newShop.setShopAddress(currentAddress);
        newShop.setOwner(currentUser);
        currentUser.setShop(newShop);  // Important for bidirectional relationship

        // Save user first (if necessary)
        userRepository.saveAndFlush(currentUser);

        // Update user privileges
        currentUser.getPrivileges().clear();
        currentUser.getPrivileges().addAll(Set.of(
                Privilege.CREATE_BOOKS,
                Privilege.EDIT_BOOKS,
                Privilege.DELETE_BOOKS,
                Privilege.EDIT_SHOP,
                Privilege.TOGGLE_SHOP_AVAILABILITY,
                Privilege.READ_ORDERS,
                Privilege.UPDATE_ORDER_STATUS_CONFIRMED,
                Privilege.UPDATE_ORDER_STATUS_DELIVERED,
                Privilege.UPDATE_ORDER_STATUS_CANCELLED
        ));

        userRepository.saveAndFlush(currentUser); // Force update

        // Prepare response
        ShopResponse res = new ShopResponse();
        res.setShopName(newShop.getShopName());
        res.setShopAddress(newShop.getShopAddress().toString());
        res.setShopFollowers((long) newShop.getFollowers().size());
        res.setShopRating(newShop.getRating());
        res.setShopOwnerId(newShop.getOwner().getId());

        return res;
    }


    public void updateShop(ShopRequest request)
    {
        User currentUser = this.userService.getCurrentUser();
        Address currentAddress = addressRepository.findByUserIdAndId(currentUser.getId(), request.getAddressId());

        if(!currentUser.getAddresses().contains(currentAddress))
            throw new AppException(ErrorCode.INVALID_ADDRESS);

        Shop currentShop = this.shopRepository.getShopByOwnerId(currentUser.getId());
        currentShop.setShopName(request.getShopName());
        currentShop.setShopAddress(currentAddress);

        shopRepository.save(currentShop);
    }

    public void updateShopAvail()
    {
        User currentUser = this.userService.getCurrentUser();
        Shop currentShop = this.shopRepository.getShopByOwnerId(currentUser.getId());
        currentShop.setAvailable(!currentShop.isAvailable());

        shopRepository.save(currentShop);
    }
}
