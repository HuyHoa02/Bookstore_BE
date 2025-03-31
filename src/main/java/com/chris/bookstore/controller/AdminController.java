package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.CategoryRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.CategoryResponse;
import com.chris.bookstore.enums.OrderStatus;
import com.chris.bookstore.service.CategoryService;
import com.chris.bookstore.service.OrderService;
import com.chris.bookstore.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final ShopService shopService;

    public AdminController(
            CategoryService categoryService,
            OrderService orderService,
            ShopService shopService
    ){
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.shopService = shopService;
    }
//-------------------------------------CATEGORY---------------------------------------------

    @PostMapping("/create-category")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request){
        CategoryResponse creationResponse = this.categoryService.handleCreateCategory(request);

        ApiResponse<CategoryResponse> res = new ApiResponse<CategoryResponse>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setResult(creationResponse);
        res.setMessage("Creating book succeed!");

        return res;
    }

    @PutMapping("/update-category/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@Valid @RequestBody CategoryRequest request,
                                                        @PathVariable Long id){
        CategoryResponse creationResponse = this.categoryService.handleUpdateCategory(request, id);

        ApiResponse<CategoryResponse> res = new ApiResponse<CategoryResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(creationResponse);
        res.setMessage("Updating category succeed!");

        return res;
    }

    @DeleteMapping("/delete-category/{id}")
    public ApiResponse<CategoryResponse> deleteCategory(@Valid @PathVariable Long id){
        this.categoryService.handleDeleteCategory(id);

        ApiResponse<CategoryResponse> res = new ApiResponse<CategoryResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Deleting category succeed!");

        return res;
    }

//----------------------------------ORDER-------------------------------------------

    @PutMapping("/update-order-delivered/{orderId}")
    public ApiResponse<Void> updateOrderStatusDelivered(@PathVariable(value = "orderId") Long orderId)
    {
        this.orderService.updateStatus(orderId, OrderStatus.DELIVERED);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating order's status delivred succeed!");

        return res;
    }
    @PutMapping("/update-order-cancelled/{orderId}")
    public ApiResponse<Void> updateOrderStatusCancelled(@PathVariable(value = "orderId") Long orderId)
    {
        this.orderService.updateStatus(orderId, OrderStatus.CANCELLED);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating order's status cancelled succeed!");

        return res;
    }

    //--------------------------------SHOP-----------------------------------------




}
