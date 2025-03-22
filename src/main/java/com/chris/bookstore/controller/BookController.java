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

    @GetMapping
    public ApiResponse<List<BookCreationResponse>> getAllBooks(){
        List<BookCreationResponse> books = this.bookService.getAllBooks();

        ApiResponse<List<BookCreationResponse>> res = new ApiResponse<List<BookCreationResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(books);
        res.setMessage("Get all books succeed!");

        return res;
    }

    @GetMapping("/{title}")
    public ApiResponse<List<BookCreationResponse>> getBook(@PathVariable(value = "title") String title){
        List<BookCreationResponse> books = this.bookService.getBookByTitle(title);

        ApiResponse<List<BookCreationResponse>> res = new ApiResponse<List<BookCreationResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(books);
        res.setMessage("Get book succeed!");

        return res;
    }

    @PostMapping
    public ApiResponse<BookCreationResponse> createBook(@Valid @RequestBody BookRequest request){
        BookCreationResponse bookCreationResponse = this.bookService.handleCreateBook(request);

        ApiResponse<BookCreationResponse> res = new ApiResponse<BookCreationResponse>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setResult(bookCreationResponse);
        res.setMessage("Creating book succeed!");

        return res;
    }

    @PutMapping("/{id}")
    public ApiResponse<BookCreationResponse> updateBook(@Valid @RequestBody BookRequest request,
                                                        @PathVariable(value = "id") Long id){
        BookCreationResponse bookCreationResponse = this.bookService.handleUpdateBook(request, id);

        ApiResponse<BookCreationResponse> res = new ApiResponse<BookCreationResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(bookCreationResponse);
        res.setMessage("Updating book succeed!");

        return res;
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBook(@PathVariable(value = "id") Long id){
        this.bookService.handleDeleteBook(id);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Deleting book succeed!");

        return res;
    }
}
