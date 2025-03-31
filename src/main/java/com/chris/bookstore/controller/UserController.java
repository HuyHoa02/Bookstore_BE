package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.*;
import com.chris.bookstore.dto.response.*;
import com.chris.bookstore.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final AddressService addressService;
    private final CartService cartService;
    private final OrderService orderService;
    private final ShopService shopService;
    private final BookService bookService;


    public UserController(AddressService addressService,
                          CartService cartService,
                          OrderService orderService,
                          ShopService shopService,
                          BookService bookService)
    {
        this.addressService = addressService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.shopService = shopService;
        this.bookService = bookService;
    }

    @GetMapping("/get-addresses/userId")
    public ApiResponse<List<AddressResponse>> getAllAddresses(@PathVariable(value = "userId") Long userId)
    {
        List<AddressResponse> list = this.addressService.getAll(userId);

        ApiResponse<List<AddressResponse>> res = new ApiResponse<List<AddressResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Getting all addresses succeed");
        res.setResult(list);

        return res;
    }

    @PostMapping("/add-address")
    public ApiResponse<Void> addAddress(@Valid @RequestBody AddressRequest request)
    {
        this.addressService.addAddress(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setMessage("Getting all addresses succeed");

        return res;
    }

    @PutMapping("/update-address/{addressId}")
    public ApiResponse<Void> updateAddress(@PathVariable(value = "addressId") Long addressId,
                                           @Valid @RequestBody AddressRequest request)
    {
        this.addressService.updateAddress(addressId, request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating all addresses succeed");

        return res;
    }

    @DeleteMapping("/delete-address/{addressId}")
    public ApiResponse<Void> deleteAddress(@PathVariable(value = "addressId") Long addressId)
    {
        this.addressService.deleteAddress(addressId);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Deleting all addresses succeed");

        return res;
    }



    @GetMapping("/get-cart-items")
    public ApiResponse<List<CartItemsResponse>> getCartItems()
    {
        List<CartItemsResponse> items = this.cartService.getCartItems();

        ApiResponse<List<CartItemsResponse>> res = new ApiResponse<List<CartItemsResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(items);
        res.setMessage("Get items succeed!");

        return res;
    }

    @PostMapping("/add-to-cart")
    public ApiResponse<Void> addToCart(@Valid @RequestBody CartItemRequest request)
    {
        this.cartService.addToCart(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Adding item to cart succeed!");

        return res;
    }

    @DeleteMapping("/remove-from-cart")
    private ApiResponse<Void> removeFromCart(@Valid @RequestBody CartItemRequest request){
        this.cartService.removeFromCart(request);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Removing item to cart succeed!");

        return res;
    }

    @PostMapping("/place-an-order")
    public ApiResponse<Void> placeAnOrder(@RequestBody @Valid placeOrderRequest request)
    {
        this.orderService.placeAnOrder(request.getUserId(), request.getAddressId());

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Placing an order succeed!");

        return res;
    }

    @PostMapping("/create-shop")
    public ApiResponse<ShopResponse> createShop(@Valid @RequestBody ShopRequest request)
    {
        ShopResponse shopResponse = this.shopService.createShop(request);

        ApiResponse<ShopResponse> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setMessage("Shop created successfully");
        res.setResult(shopResponse);

        return res;
    }

    @PutMapping("/update-shop")
    public ApiResponse<Void> updateShop(@Valid @RequestBody ShopRequest request)
    {
        this.shopService.updateShop(request);

        ApiResponse<Void> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Shop updated successfully");

        return res;
    }

    @PutMapping("/toggle-shop-availability")
    public ApiResponse<Void> updateShopAvailability()
    {
        this.shopService.updateShopAvai();

        ApiResponse<Void> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Shop updated successfully");

        return res;
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
