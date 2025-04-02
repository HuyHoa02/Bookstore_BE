package com.chris.bookstore.service;

import com.chris.bookstore.dto.response.CartItemsResponse;
import com.chris.bookstore.entity.Book;
import com.chris.bookstore.entity.Cart;
import com.chris.bookstore.entity.CartItems;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.BookRepository;
import com.chris.bookstore.repository.CartItemsRepository;
import com.chris.bookstore.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final BookRepository bookRepository;
    private final UserService userService;


    public CartService(CartRepository cartRepository,
                       CartItemsRepository cartItemsRepository,
                       BookRepository bookRepository,
                       UserService userService)
    {
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    public void addToCart(Long bookId) {
        User currentUser = this.userService.getCurrentUser();
        Cart existingCart = currentUser.getCart();

        if (existingCart == null) {
            throw new AppException(ErrorCode.CART_NOT_EXISTED);
        }

        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        CartItems cartItem = cartItemsRepository.findByCartIdAndBookId(existingCart.getId(), book.getId());

        if (cartItem == null) {
            CartItems newItem = new CartItems();
            newItem.setCart(existingCart);  // Ensure bidirectional mapping
            newItem.setBook(book);
            newItem.setQuantity(1);
            newItem.setUnitPrice(book.getPrice());

            // Save the new CartItem first to ensure it's recognized
            cartItemsRepository.save(newItem);

            // Add it to the cart
            existingCart.getCartItems().add(newItem);
            existingCart.setTotalAmount(existingCart.getTotalAmount() + newItem.getUnitPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemsRepository.save(cartItem);
            existingCart.setTotalAmount(existingCart.getTotalAmount() + cartItem.getUnitPrice());
        }

        // Save the cart after modifying it
        cartRepository.save(existingCart);
    }


    public void removeFromCart(Long bookId)
    {
        User currentUser = this.userService.getCurrentUser();
        Cart existingCart = currentUser.getCart();

        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        CartItems cartItem = cartItemsRepository.findByCartIdAndBookId(existingCart.getId(), book.getId());

        if(cartItem.getQuantity() <= 0)
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        else {
            cartItem.setQuantity(cartItem.getQuantity() - 1);

            existingCart.setTotalAmount(existingCart.getTotalAmount() - cartItem.getUnitPrice());
        }
        cartItemsRepository.save(cartItem);
        cartRepository.save(existingCart);
    }

    public List<CartItemsResponse> getCartItems(){
        User currentUser = this.userService.getCurrentUser();
        Cart currentCart = currentUser.getCart();

        return currentCart.getCartItems().stream().map(CartItemsResponse::new).toList();
    }
}
