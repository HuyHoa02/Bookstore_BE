package com.chris.bookstore.controller;

import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.service.CartService;
import com.chris.bookstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService,
                           CartService cartService)
    {
        this.orderService = orderService;
    }

    @PostMapping("/{cartId}/{addressId}")
    public ApiResponse<Void> placeAnOrder(@PathVariable(value = "cartId") Long cartId,
                                          @PathVariable(value = "addressId")Long addressId)
    {
        this.orderService.placeAnOrder(cartId, addressId);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Placing an order succeed!");

        return res;
    }

}
