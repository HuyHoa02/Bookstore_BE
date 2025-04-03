package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.CategoryRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.CategoryResponse;
import com.chris.bookstore.enums.OrderStatus;
import com.chris.bookstore.service.CategoryService;
import com.chris.bookstore.service.OrderService;
import com.chris.bookstore.service.ShopService;
import com.chris.bookstore.util.Helper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final Helper helper;

    public AdminController(
            CategoryService categoryService,
            OrderService orderService,
            Helper helper
    ){
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.helper = helper;
    }

    /**  CATEGORIES   **/
    @PostMapping("/categories/create")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request){
        return helper.buildResponse(HttpStatus.CREATED,"Creating category succeed",this.categoryService.handleCreateCategory(request));
    }

    @PutMapping("/categories/update/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@Valid @RequestBody CategoryRequest request,
                                                        @PathVariable Long id){
        return helper.buildResponse(HttpStatus.OK,"Updating category succeed",this.categoryService.handleUpdateCategory(request, id));
    }

    @DeleteMapping("/categories/delete/{id}")
    public ApiResponse<Void> deleteCategory(@Valid @PathVariable Long id){
        this.categoryService.handleDeleteCategory(id);
        return helper.buildResponse(HttpStatus.OK,"Deleting category succeed", null);
    }

    /**  ORDERS   **/
    @PutMapping("/orders/update/{orderId}/shipped")
    public ApiResponse<Void> updateOrderStatusShipped(@PathVariable(value = "orderId") Long orderId)
    {
        this.orderService.handleUpdateStatus(orderId, OrderStatus.SHIPPED);
        return helper.buildResponse(HttpStatus.OK,"Updating order's status shipped succeed!", null);
    }

    @PutMapping("orders/update/{orderId}/cancelled")
    public ApiResponse<Void> updateOrderStatusCancelled(@PathVariable(value = "orderId") Long orderId)
    {
        this.orderService.handleUpdateStatus(orderId, OrderStatus.CANCELLED);
        return helper.buildResponse(HttpStatus.OK,"Updating order's status cancelled succeed!", null);
    }
}
