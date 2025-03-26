package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.AddressRequest;
import com.chris.bookstore.dto.request.BookRequest;
import com.chris.bookstore.dto.response.AddressResponse;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.BookCreationResponse;
import com.chris.bookstore.service.AddressService;
import com.chris.bookstore.service.BookService;
import com.chris.bookstore.service.CategoryService;
import com.chris.bookstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/shops")
public class ShopController {

    private final BookService bookService;

    public ShopController(
            BookService bookService
    ){
        this.bookService = bookService;
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

}
