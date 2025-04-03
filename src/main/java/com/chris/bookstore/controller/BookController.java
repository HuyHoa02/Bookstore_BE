package com.chris.bookstore.controller;

import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.BookResponse;
import com.chris.bookstore.service.BookService;
import com.chris.bookstore.util.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final Helper helper;

    public BookController(
            BookService bookService,
            Helper helper
    ){
        this.bookService = bookService;
        this.helper = helper;
    }

    @GetMapping("/all")
    public ApiResponse<List<BookResponse>> getAllBooks(){
        return helper.buildResponse(HttpStatus.OK,"Get all books succeed!",this.bookService.getAllBooks());
    }

    @GetMapping("/by-title/{title}")
    public ApiResponse<List<BookResponse>> getBook(@PathVariable(value = "title") String title){
        return helper.buildResponse(HttpStatus.OK,"Get books succeed!",this.bookService.getBookByTitle(title));

    }

}
