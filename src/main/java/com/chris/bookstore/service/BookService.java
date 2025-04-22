package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.*;
import com.chris.bookstore.dto.response.BookResponse;
import com.chris.bookstore.entity.*;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.BookRatingRepository;
import com.chris.bookstore.repository.BookRepository;
import com.chris.bookstore.repository.CategoryRepository;
import com.chris.bookstore.repository.OrderDetailsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final OrderDetailsRepository orderDetailsRepository;
    private final BookRatingRepository bookRatingRepository;

    public BookService(
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            CloudinaryService cloudinaryService,
            UserService userService,
            OrderDetailsRepository orderDetailsRepository,
            BookRatingRepository bookRatingRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
        this.orderDetailsRepository = orderDetailsRepository;
        this.bookRatingRepository = bookRatingRepository;
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

    public List<BookResponse> getBookByShop(Long id){
        return this.bookRepository.findByShopId(id).stream().map(BookResponse::new).toList();
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

    public BookResponse handleUpdateBookStock(Long id, UpdateStockRequest request)
    {
        User currentUser = userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();
        Book existingBook = this.bookRepository.getBookByIdAndShopId(id, currentShop.getId());
        if(existingBook == null)
            throw new AppException(ErrorCode.BOOK_NOT_EXISTED);

        existingBook.setStock(request.getStock());

        bookRepository.save(existingBook);

        return new BookResponse(existingBook);
    }

    public BookResponse handleUpdateBookPrice(Long id, UpdatePriceRequest request)
    {
        User currentUser = userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();
        Book existingBook = this.bookRepository.getBookByIdAndShopId(id, currentShop.getId());
        if(existingBook == null)
            throw new AppException(ErrorCode.BOOK_NOT_EXISTED);

        existingBook.setPrice(request.getPrice());

        bookRepository.save(existingBook);

        return new BookResponse(existingBook);
    }

    public BookResponse handleUpdateBookImage(Long id, MultipartFile file) throws IOException {
        User currentUser = userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();
        Book existingBook = this.bookRepository.getBookByIdAndShopId(id, currentShop.getId());
        if(existingBook == null)
            throw new AppException(ErrorCode.BOOK_NOT_EXISTED);

        String imageUrl = cloudinaryService.uploadFile(file).get("url").toString();

        existingBook.setImageUrl(imageUrl);

        bookRepository.save(existingBook);

        return new BookResponse(existingBook);
    }

    public BookResponse handleUpdateBookInfo(Long id, UpdateBookInfoRequest request) {
        User currentUser = userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();
        Book existingBook = this.bookRepository.getBookByIdAndShopId(id, currentShop.getId());
        if(existingBook == null)
            throw new AppException(ErrorCode.BOOK_NOT_EXISTED);

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setDescription(request.getDescription());

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

    public void handleRateBook(Long bookId, RatingRequest request) {
        User currentUser = userService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        boolean hasBought = orderDetailsRepository.hasUserBoughtBook(currentUser.getId(), bookId);
        if (!hasBought) {
            throw new AppException(ErrorCode.BOOK_NOT_PURCHASED);
        }

        // Optional: check if already rated
        Optional<BookRating> existing = bookRatingRepository.findByUserIdAndBookId(currentUser.getId(), bookId);
        if (existing.isPresent()) {
            throw new AppException(ErrorCode.BOOK_ALREADY_RATED);
        }

        // Save new rating
        BookRating bookRating = new BookRating();
        bookRating.setBook(book);
        bookRating.setUser(currentUser);
        bookRating.setRating(request.getRating());
        bookRating.setReview(request.getReview());
        bookRatingRepository.save(bookRating);
    }

}