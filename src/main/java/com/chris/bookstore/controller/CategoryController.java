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

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request){
        CategoryResponse creationResponse = this.categoryService.handleCreateCategory(request);

        ApiResponse<CategoryResponse> res = new ApiResponse<CategoryResponse>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setResult(creationResponse);
        res.setMessage("Creating book succeed!");

        return res;
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@Valid @RequestBody CategoryRequest request,
                                                        @PathVariable Long id){
        CategoryResponse creationResponse = this.categoryService.handleUpdateCategory(request, id);

        ApiResponse<CategoryResponse> res = new ApiResponse<CategoryResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(creationResponse);
        res.setMessage("Updating category succeed!");

        return res;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<CategoryResponse> deleteCategory(@Valid @PathVariable Long id){
        this.categoryService.handleDeleteCategory(id);

        ApiResponse<CategoryResponse> res = new ApiResponse<CategoryResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Deleting category succeed!");

        return res;
    }
}
