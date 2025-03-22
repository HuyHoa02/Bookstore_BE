package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.CartItemRequest;
import com.chris.bookstore.dto.response.BookCreationResponse;
import com.chris.bookstore.dto.response.CartItemsResponse;
import com.chris.bookstore.entity.Book;
import com.chris.bookstore.entity.Cart;
import com.chris.bookstore.entity.CartItems;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.BookRepository;
import com.chris.bookstore.repository.CartItemsRepository;
import com.chris.bookstore.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final BookRepository bookRepository;

    public CartService(CartRepository cartRepository,
                       CartItemsRepository cartItemsRepository,
                       BookRepository bookRepository)
    {
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.bookRepository = bookRepository;
    }

    public void addToCart(CartItemRequest request)
    {
        Cart existingCart = this.cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        Book book = this.bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        CartItems cartItem = cartItemsRepository.findByCartIdAndBookId(existingCart.getId(), book.getId());

        if(cartItem == null)
        {

            CartItems newItem = new CartItems();
            newItem.setCart(existingCart);
            newItem.setBook(book);
            newItem.setQuantity(1);
            newItem.setUnitPrice(book.getPrice());

            existingCart.getCartItems().add(newItem);
            existingCart.setTotalAmount(existingCart.getTotalAmount() + newItem.getUnitPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemsRepository.save(cartItem);
            existingCart.setTotalAmount(existingCart.getTotalAmount() + cartItem.getUnitPrice());
        }
        cartRepository.save(existingCart);
    }

    public void removeFromCart(CartItemRequest request)
    {
        Cart existingCart = this.cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        Book book = this.bookRepository.findById(request.getBookId())
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
        return this.cartItemsRepository.findAll().stream().map(item -> {
            return new CartItemsResponse(item);
        }).toList();
    }
}
