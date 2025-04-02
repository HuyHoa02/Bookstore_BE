package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.BookRequest;
import com.chris.bookstore.dto.response.BookResponse;
import com.chris.bookstore.entity.Book;
import com.chris.bookstore.entity.Category;
import com.chris.bookstore.entity.Shop;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.BookRepository;
import com.chris.bookstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    public BookService(
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            CloudinaryService cloudinaryService,
            UserService userService) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
    }
    //-------------------------------- BOOK -------------------------------------------------

    public BookResponse handleCreateBook(BookRequest request, MultipartFile file) throws IOException {
        Category category = this.categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        User currentUser = this.userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setCategory(category);
        book.setImageUrl(cloudinaryService.uploadFile(file).get("url").toString());
        book.setShop(currentShop);

        bookRepository.save(book);

        return new BookResponse(book);
    }

    public List<BookResponse> getAllBooks(){
        return this.bookRepository.findAll().stream().map(BookResponse::new).toList();
    }

    public List<BookResponse> getBookByTitle(String title){
        return this.bookRepository.findByTitle(title).stream().map(BookResponse::new).toList();
    }

    public BookResponse handleUpdateBook(BookRequest request, MultipartFile file, Long bookId) throws IOException {
        Category category = this.categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        User currentUser = this.userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();
        Book existingBook = this.bookRepository.getBookByIdAndShopId(bookId, currentShop.getId());
        if(existingBook == null)
            throw new AppException(ErrorCode.BOOK_NOT_EXISTED);

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setDescription(request.getDescription());
        existingBook.setPrice(request.getPrice());
        existingBook.setStock(request.getStock());
        existingBook.setCategory(category);
        existingBook.setImageUrl(cloudinaryService.uploadFile(file).get("url").toString());

        bookRepository.save(existingBook);

        return new BookResponse(existingBook);
    }

    public void handleDeleteBook(Long bookId){
        User currentUser = this.userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();

        Book existingBook = this.bookRepository.getBookByIdAndShopId(bookId, currentShop.getId());
        if(existingBook == null)
            throw new AppException(ErrorCode.BOOK_NOT_EXISTED);

        this.bookRepository.deleteById(bookId);
    }
}