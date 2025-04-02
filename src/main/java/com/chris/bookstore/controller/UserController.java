package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.*;
import com.chris.bookstore.dto.response.*;
import com.chris.bookstore.enums.OrderStatus;
import com.chris.bookstore.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/get-addresses")
    public ApiResponse<List<AddressResponse>> getAllAddresses()
    {
        List<AddressResponse> list = this.addressService.getAllAddresses();

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
        res.setMessage("Adding addresses successful");

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

    @PostMapping("/add-to-cart/{bookId}")
    public ApiResponse<Void> addToCart(@PathVariable(value = "bookId")Long bookId)
    {
        this.cartService.addToCart(bookId);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Adding item to cart succeed!");

        return res;
    }

    @DeleteMapping("/remove-from-cart/{bookId}")
    private ApiResponse<Void> removeFromCart(@PathVariable(value = "bookId")Long bookId){
        this.cartService.removeFromCart(bookId);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Removing item to cart succeed!");

        return res;
    }

    @PostMapping("/place-an-order/{addressId}")
    public ApiResponse<Void> placeAnOrder(@PathVariable(value = "addressId") Long addressId)
    {
        this.orderService.placeAnOrder(addressId);

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
    @PreAuthorize("hasAuthority('EDIT_SHOP')")
    public ApiResponse<Void> updateShop(@Valid @RequestBody ShopRequest request)
    {
        this.shopService.updateShop(request);

        ApiResponse<Void> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Shop updated successfully");

        return res;
    }

    @PutMapping("/toggle-shop-availability")
    @PreAuthorize("hasAuthority('TOGGLE_SHOP_AVAILABILITY')")
    public ApiResponse<Void> updateShopAvailability()
    {
        this.shopService.updateShopAvail();

        ApiResponse<Void> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Shop updated successfully");

        return res;
    }

    @PostMapping("/create-book")
    @PreAuthorize("hasAuthority('CREATE_BOOKS')")
    public ApiResponse<BookResponse> createBook(
            @Valid @RequestPart("book") BookRequest request,
            @RequestPart("image") MultipartFile file) throws IOException {
        BookResponse bookResponse = this.bookService.handleCreateBook(request, file);

        ApiResponse<BookResponse> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setResult(bookResponse);
        res.setMessage("Creating book succeed!");

        return res;
    }

    @PutMapping("/update-book/{id}")
    @PreAuthorize("hasAuthority('EDIT_BOOKS')")
    public ApiResponse<BookResponse> updateBook(@Valid @RequestBody BookRequest request,
                                                @RequestParam(name = "image") MultipartFile file,
                                                @PathVariable(value = "id") Long id) throws IOException {
        BookResponse bookResponse = this.bookService.handleUpdateBook(request,file, id);

        ApiResponse<BookResponse> res = new ApiResponse<BookResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setResult(bookResponse);
        res.setMessage("Updating book succeed!");

        return res;
    }


    @DeleteMapping("/delete-book/{id}")
    @PreAuthorize("hasAuthority('DELETE_BOOKS')")
    public ApiResponse<Void> deleteBook(@PathVariable(value = "id") Long id){
        this.bookService.handleDeleteBook(id);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Deleting book succeed!");

        return res;
    }

    @GetMapping("/get-all-orders")
    @PreAuthorize("hasAuthority('READ_ORDERS')")
    public ApiResponse<List<OrderResponse>> getAllOrders()
    {
        List<OrderResponse> responseList = this.orderService.getAllOrders();

        ApiResponse<List<OrderResponse>> res = new ApiResponse<List<OrderResponse>>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Get all orders succeed!");
        res.setResult(responseList);

        return res;
    }

    @PutMapping("/update-order-confirmed/{orderId}")
    @PreAuthorize("hasAuthority('UPDATE_ORDER_STATUS_CONFIRMED')")
    public ApiResponse<Void> updateOrderStatusConfirmed(@PathVariable(value = "orderId") Long orderId)
    {
        this.orderService.updateStatus(orderId, OrderStatus.CONFIRMED);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating order's status confirmed succeed!");

        return res;
    }
    @PutMapping("/update-order-delivered/{orderId}")
    @PreAuthorize("hasAuthority('UPDATE_ORDER_STATUS_DELIVERED')")
    public ApiResponse<Void> updateOrderStatusShipped(@PathVariable(value = "orderId") Long orderId)
    {
        this.orderService.updateStatus(orderId, OrderStatus.SHIPPED);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating order's status delivered succeed!");

        return res;
    }
    @PutMapping("/update-order-cancelled/{orderId}")
    @PreAuthorize("hasAuthority('UPDATE_ORDER_STATUS_CANCELLED')")
    public ApiResponse<Void> updateOrderStatusCancelled(@PathVariable(value = "orderId") Long orderId)
    {
        this.orderService.updateStatus(orderId, OrderStatus.CANCELLED);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Updating order's status delivered succeed!");

        return res;
    }
}
