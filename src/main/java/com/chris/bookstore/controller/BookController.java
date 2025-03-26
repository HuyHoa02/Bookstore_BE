package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.BookRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.BookCreationResponse;
import com.chris.bookstore.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(
            BookService bookService
    ){
        this.bookService = bookService;
    }

    @GetMapping("get-all")
    public ApiResponse<List<BookCreationResponse>> getAllBooks(){
        List<BookCreationResponse> books = this.bookService.getAllBooks();

        ApiResponse<List<BookCreationResponse>> res = new ApiResponse<List<BookCreationResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(books);
        res.setMessage("Get all books succeed!");

        return res;
    }

    @GetMapping("/get-by-title/{title}")
    public ApiResponse<List<BookCreationResponse>> getBook(@PathVariable(value = "title") String title){
        List<BookCreationResponse> books = this.bookService.getBookByTitle(title);

        ApiResponse<List<BookCreationResponse>> res = new ApiResponse<List<BookCreationResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(books);
        res.setMessage("Get book succeed!");

        return res;
    }

}
