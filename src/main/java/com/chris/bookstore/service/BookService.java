package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.BookRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.BookCreationResponse;
import com.chris.bookstore.entity.Book;
import com.chris.bookstore.entity.Category;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.BookRepository;
import com.chris.bookstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(
            BookRepository bookRepository,
            CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }
    //-------------------------------- BOOK -------------------------------------------------

    public BookCreationResponse handleCreateBook(BookRequest request) {
        Category category = this.categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setCategory(category);
        book.setImageUrl(request.getImageUrl());

        bookRepository.save(book);

        return new BookCreationResponse(book);
    }

    public List<BookCreationResponse> getAllBooks(){
        return this.bookRepository.findAll().stream().map(book -> {
            return new BookCreationResponse(book);
        }).toList();
    }

    public List<BookCreationResponse> getBookByTitle(String title){
        return this.bookRepository.findByTitle(title).stream().map(book -> {
            return new BookCreationResponse(book);
        }).toList();
    }

    public BookCreationResponse handleUpdateBook(BookRequest request, Long id) {
        Category category = this.categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Book existingBook = this.bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setDescription(request.getDescription());
        existingBook.setPrice(request.getPrice());
        existingBook.setStock(request.getStock());
        existingBook.setCategory(category);
        existingBook.setImageUrl(request.getImageUrl());

        bookRepository.save(existingBook);

        return new BookCreationResponse(existingBook);
    }

    public void handleDeleteBook(Long id){
        Book existingBook = this.bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        this.bookRepository.deleteById(id);
    }
}