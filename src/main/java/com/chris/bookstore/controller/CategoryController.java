package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.CategoryRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.CategoryResponse;
import com.chris.bookstore.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ){
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories(){
        List<CategoryResponse> categories = this.categoryService.getAllCategories();

        ApiResponse<List<CategoryResponse>> res = new ApiResponse<List<CategoryResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(categories);
        res.setMessage("Getting all categories succeed!");

        return res;
    }

}
