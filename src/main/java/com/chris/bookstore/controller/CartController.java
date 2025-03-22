package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.CartItemRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.BookCreationResponse;
import com.chris.bookstore.dto.response.CartItemsResponse;
import com.chris.bookstore.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService)
    {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<List<CartItemsResponse>> getCartItems()
    {
        List<CartItemsResponse> items = this.cartService.getCartItems();

        ApiResponse<List<CartItemsResponse>> res = new ApiResponse<List<CartItemsResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(items);
        res.setMessage("Get items succeed!");

        return res;
    }

    @PostMapping
    public ApiResponse<Void> addToCart(@Valid @RequestBody CartItemRequest request)
    {
        this.cartService.addToCart(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Adding item to cart succeed!");

        return res;
    }

    @DeleteMapping
    private ApiResponse<Void> removeFromCart(@Valid @RequestBody CartItemRequest request){
        this.cartService.removeFromCart(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Removing item to cart succeed!");

        return res;
    }
}
