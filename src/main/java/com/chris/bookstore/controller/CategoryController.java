package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.CategoryRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.CategoryResponse;
import com.chris.bookstore.service.CategoryService;
import com.chris.bookstore.util.Helper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final Helper helper;

    public CategoryController(
            CategoryService categoryService,
            Helper helper
    ){
        this.categoryService = categoryService;
        this.helper = helper;
    }

    @GetMapping("/all")
    public ApiResponse<List<CategoryResponse>> getAllCategories(){
        return helper.buildResponse(HttpStatus.OK,"Getting all categories succeed!",this.categoryService.getAllCategories());
    }
}
