package com.chris.bookstore.controller;

import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.BookResponse;
import com.chris.bookstore.service.BookService;
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
    public ApiResponse<List<BookResponse>> getAllBooks(){
        List<BookResponse> books = this.bookService.getAllBooks();

        ApiResponse<List<BookResponse>> res = new ApiResponse<List<BookResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(books);
        res.setMessage("Get all books succeed!");

        return res;
    }

    @GetMapping("/get-by-title/{title}")
    public ApiResponse<List<BookResponse>> getBook(@PathVariable(value = "title") String title){
        List<BookResponse> books = this.bookService.getBookByTitle(title);

        ApiResponse<List<BookResponse>> res = new ApiResponse<List<BookResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(books);
        res.setMessage("Get book succeed!");

        return res;
    }

}
