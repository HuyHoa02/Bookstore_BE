package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.BookRequest;
import com.chris.bookstore.dto.request.CategoryRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.BookCreationResponse;
import com.chris.bookstore.dto.response.CategoryResponse;
import com.chris.bookstore.service.BookService;
import com.chris.bookstore.service.CategoryService;
import com.chris.bookstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    public AdminController(
            BookService bookService,
            CategoryService categoryService,
            OrderService orderService
    ){
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    @PostMapping(value = "/create-book")
    public ApiResponse<BookCreationResponse> createBook(
            @Valid @RequestPart("book") BookRequest request,
            @RequestPart("image") MultipartFile file) throws IOException {
        BookCreationResponse bookCreationResponse = this.bookService.handleCreateBook(request, file);

        ApiResponse<BookCreationResponse> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setResult(bookCreationResponse);
        res.setMessage("Creating book succeed!");

        return res;
    }

    @PutMapping("/update-book/{id}")
    public ApiResponse<BookCreationResponse> updateBook(@Valid @RequestBody BookRequest request,
                                                        @RequestParam(name = "image") MultipartFile file,
                                                        @PathVariable(value = "id") Long id) throws IOException {
        BookCreationResponse bookCreationResponse = this.bookService.handleUpdateBook(request,file, id);

        ApiResponse<BookCreationResponse> res = new ApiResponse<BookCreationResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(bookCreationResponse);
        res.setMessage("Updating book succeed!");

        return res;
    }


    @DeleteMapping("/delete-book/{id}")
    public ApiResponse<Void> deleteBook(@PathVariable(value = "id") Long id){
        this.bookService.handleDeleteBook(id);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Deleting book succeed!");

        return res;
    }

//-------------------------------------CART---------------------------------------------

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

    @PutMapping("/update-order/{orderId}/{option}")
    public ApiResponse<Void> updateOrderStatus(@PathVariable(value = "orderId") Long orderId
            ,@PathVariable(value = "option") int option)
    {
        this.orderService.updateStatus(orderId, option);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating order's status succeed!");

        return res;
    }
}
