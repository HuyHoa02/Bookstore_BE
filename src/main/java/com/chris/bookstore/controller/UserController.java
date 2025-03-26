package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.AddressRequest;
import com.chris.bookstore.dto.request.CartItemRequest;
import com.chris.bookstore.dto.request.placeOrderRequest;
import com.chris.bookstore.dto.response.AddressResponse;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.CartItemsResponse;
import com.chris.bookstore.service.AddressService;
import com.chris.bookstore.service.CartService;
import com.chris.bookstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final AddressService addressService;
    private final CartService cartService;
    private final OrderService orderService;

    public UserController(AddressService addressService,
                          CartService cartService,
                          OrderService orderService)
    {
        this.addressService = addressService;
        this.cartService = cartService;
        this.orderService = orderService;

    }

    @GetMapping("/get-addresses/userId")
    public ApiResponse<List<AddressResponse>> getAllAddresses(@PathVariable(value = "userId") Long userId)
    {
        List<AddressResponse> list = this.addressService.getAll(userId);

        ApiResponse<List<AddressResponse>> res = new ApiResponse<List<AddressResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Getting all addresses succeed");
        res.setResult(list);

        return res;
    }

    @PostMapping("/add-address")
    public ApiResponse<Void> addAddress(@Valid @RequestBody AddressRequest request)
    {
        this.addressService.addAddress(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setMessage("Getting all addresses succeed");

        return res;
    }

    @PutMapping("/update-address/{addressId}")
    public ApiResponse<Void> updateAddress(@PathVariable(value = "addressId") Long addressId,
                                           @Valid @RequestBody AddressRequest request)
    {
        this.addressService.updateAddress(addressId, request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating all addresses succeed");

        return res;
    }

    @DeleteMapping("/delete-address/{addressId}")
    public ApiResponse<Void> updateAddress(@PathVariable(value = "addressId") Long addressId)
    {
        this.addressService.deleteAddress(addressId);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Deleting all addresses succeed");

        return res;
    }



    @GetMapping("/get-cart-items")
    public ApiResponse<List<CartItemsResponse>> getCartItems()
    {
        List<CartItemsResponse> items = this.cartService.getCartItems();

        ApiResponse<List<CartItemsResponse>> res = new ApiResponse<List<CartItemsResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(items);
        res.setMessage("Get items succeed!");

        return res;
    }

    @PostMapping("/add-to-cart")
    public ApiResponse<Void> addToCart(@Valid @RequestBody CartItemRequest request)
    {
        this.cartService.addToCart(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Adding item to cart succeed!");

        return res;
    }

    @DeleteMapping("/remove-from-cart")
    private ApiResponse<Void> removeFromCart(@Valid @RequestBody CartItemRequest request){
        this.cartService.removeFromCart(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Removing item to cart succeed!");

        return res;
    }

    @PostMapping("/place-an-order")
    public ApiResponse<Void> placeAnOrder(@RequestBody @Valid placeOrderRequest request)
    {
        this.orderService.placeAnOrder(request.getUserId(), request.getAddressId());

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Placing an order succeed!");

        return res;
    }

}
