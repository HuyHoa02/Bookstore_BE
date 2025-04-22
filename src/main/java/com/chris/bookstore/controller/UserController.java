package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.*;
import com.chris.bookstore.dto.response.*;
import com.chris.bookstore.enums.OrderStatus;
import com.chris.bookstore.service.*;
import com.chris.bookstore.util.Helper;
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
    private final ShopRatingService shopRatingService;
    private final UserService userService;
    private final Helper helper;


    public UserController(AddressService addressService,
                          CartService cartService,
                          OrderService orderService,
                          ShopService shopService,
                          BookService bookService,
                          ShopRatingService shopRatingService,
                          UserService userService,
                          Helper helper)
    {
        this.addressService = addressService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.shopService = shopService;
        this.bookService = bookService;
        this.shopRatingService = shopRatingService;
        this.userService = userService;
        this.helper = helper;
    }

    /** ADDRESSES **/
    @GetMapping("/addresses/all")
    @PreAuthorize("hasAuthority('GET_ADDRESSES')")
    public ApiResponse<List<AddressResponse>> getAllAddresses()
    {
        return helper.buildResponse(HttpStatus.OK,"Getting all addresses succeed",addressService.getAllAddresses());
    }

    @PostMapping("/addresses/add")
    @PreAuthorize("hasAuthority('ADD_ADDRESSES')")
    public ApiResponse<Void> addAddress(@Valid @RequestBody AddressRequest request)
    {
        addressService.addAddress(request);
        return helper.buildResponse(HttpStatus.CREATED,"Adding addresses successful", null);

    }

    @PutMapping("/addresses/update/{addressId}")
    @PreAuthorize("hasAuthority('UPDATE_ADDRESSES')")
    public ApiResponse<Void> updateAddress(@PathVariable(value = "addressId") Long addressId,
                                           @Valid @RequestBody AddressRequest request)
    {
        addressService.updateAddress(addressId, request);
        return helper.buildResponse(HttpStatus.OK,"Updating all addresses succeed", null);

    }

    @DeleteMapping("/addresses/delete/{addressId}")
    @PreAuthorize("hasAuthority('DELETE_ADDRESSES')")
    public ApiResponse<Void> deleteAddress(@PathVariable(value = "addressId") Long addressId)
    {
        addressService.deleteAddress(addressId);
        return helper.buildResponse(HttpStatus.OK,"Deleting addresses succeed", null);

    }
    /** CART **/
    @GetMapping("/cart/items")
    @PreAuthorize("hasAuthority('GET_ITEMS')")
    public ApiResponse<List<CartItemsResponse>> getCartItems()
    {
        return helper.buildResponse(HttpStatus.OK,"Get items succeed!", cartService.getCartItems());
    }

    @PostMapping("/cart/add/{bookId}")
    @PreAuthorize("hasAuthority('ADD_TO_CART')")
    public ApiResponse<Void> addToCart(@PathVariable(value = "bookId")Long bookId)
    {
        cartService.addToCart(bookId);
        return helper.buildResponse(HttpStatus.OK,"Adding item to cart succeed!", null);

    }

    @DeleteMapping("/cart/remove/{bookId}")
    @PreAuthorize("hasAuthority('REMOVE_FROM_CART')")
    private ApiResponse<Void> removeFromCart(@PathVariable(value = "bookId")Long bookId){
        cartService.removeFromCart(bookId);
        return helper.buildResponse(HttpStatus.OK,"Removing item to cart succeed!", null);
    }

    /** ORDERS **/
    @PostMapping("/orders/place")
    @PreAuthorize("hasAuthority('PLACE_ORDER')")
    public ApiResponse<Void> placeAnOrder(@RequestBody @Valid PlaceOrderRequest request)
    {
        orderService.placeOrder(request);
        return helper.buildResponse(HttpStatus.OK,"Placing an order succeed!", null);
    }


    @GetMapping("orders/all")
    @PreAuthorize("hasAuthority('READ_ORDERS')")
    public ApiResponse<List<OrderResponse>> getAllOrders()
    {
        return helper.buildResponse(HttpStatus.OK,"Get all orders succeed!", orderService.getAllOrders());
    }

    @GetMapping("orders/by-status/{index}")
    @PreAuthorize("hasAuthority('READ_ORDERS')")
    public ApiResponse<List<OrderResponse>> getOrdersByStatus(@PathVariable(name = "index") Integer index) {
        OrderStatus status = OrderStatus.fromCode(index); // Convert int to enum
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return helper.buildResponse(HttpStatus.OK, "Get all orders succeed!", orders);
    }


    @PutMapping("orders/update/{orderId}/confirmed")
    @PreAuthorize("hasAuthority('UPDATE_ORDER_STATUS_CONFIRMED')")
    public ApiResponse<Void> updateOrderStatusConfirmed(@PathVariable(value = "orderId") Long orderId)
    {
        orderService.handleUpdateStatus(orderId, OrderStatus.CONFIRMED);
        return  helper.buildResponse(HttpStatus.OK,"Updating order's status confirmed succeed!", null);
    }
    @PutMapping("/orders/update/{orderId}/delivered")
    @PreAuthorize("hasAuthority('UPDATE_ORDER_STATUS_DELIVERED')")
    public ApiResponse<Void> updateOrderStatusShipped(@PathVariable(value = "orderId") Long orderId)
    {
        orderService.handleUpdateStatus(orderId, OrderStatus.SHIPPED);
        return  helper.buildResponse(HttpStatus.OK,"Updating order's status delivered succeed!", null);
    }
    @PutMapping("orders/update/{orderId}/cancelled")
    @PreAuthorize("hasAuthority('UPDATE_ORDER_STATUS_CANCELLED')")
    public ApiResponse<Void> updateOrderStatusCancelled(@PathVariable(value = "orderId") Long orderId)
    {
        orderService.handleUpdateStatus(orderId, OrderStatus.CANCELLED);
        return  helper.buildResponse(HttpStatus.OK,"Updating order's status cancelled succeed!", null);
    }

    /** SHOP **/
    @GetMapping("/shop")
    public ApiResponse<ShopResponse> getShop(){
        return helper.buildResponse(HttpStatus.OK,"Getting shop's info successfully", shopService.getShop());
    }

    @PostMapping("/shop/create")
    @PreAuthorize("hasAuthority('CREATE_SHOP')")
    public ApiResponse<ShopResponse> createShop(@Valid @RequestPart("shop") ShopRequest request,
                                                @RequestPart("file") MultipartFile file ) throws IOException {
        return helper.buildResponse(HttpStatus.CREATED,"Shop created successfully", shopService.createShop(request, file));
    }

    @PutMapping("/shop/update")
    @PreAuthorize("hasAuthority('EDIT_SHOP')")
    public ApiResponse<Void> updateShop(@Valid @RequestBody ShopRequest request)
    {
        shopService.updateShop(request);
        return helper.buildResponse(HttpStatus.OK,"Shop updated successfully", null);
    }

    @PutMapping("/shop/toggle-availability")
    @PreAuthorize("hasAuthority('TOGGLE_SHOP_AVAILABILITY')")
    public ApiResponse<Void> updateShopAvailability()
    {
        shopService.updateShopAvail();
        return helper.buildResponse(HttpStatus.OK,"Shop updated successfully", null);
    }

    @PostMapping("/rate-shop/{shopId}")
    @PreAuthorize("hasAuthority('RATE_SHOP')")
    public ApiResponse<Void> rateShop(@PathVariable Long shopId,
                                      @Valid @RequestBody RatingRequest request) {
        shopRatingService.rateShop(shopId, request.getRating(), request.getReview());
        return  helper.buildResponse(HttpStatus.CREATED,"Rated succeed", null);
    }

    @PostMapping("/follow-shop/{shopId}")
    @PreAuthorize("hasAuthority('FOLLOW_SHOP')")
    public ApiResponse<Void> followShop(@PathVariable Long shopId) {
        userService.followShop(shopId);
        return  helper.buildResponse(HttpStatus.CREATED,"Rated succeed", null);
    }
    /** BOOKS **/
    @PostMapping("/books/create")
    @PreAuthorize("hasAuthority('CREATE_BOOKS')")
    public ApiResponse<BookResponse> createBook(
            @Valid @RequestPart("book") BookRequest request,
            @RequestPart("image") MultipartFile file) throws IOException {
        return helper.buildResponse(HttpStatus.CREATED,"Creating book succeed!", bookService.handleCreateBook(request, file));
    }

    @PutMapping("/books/update/{id}")
    @PreAuthorize("hasAuthority('EDIT_BOOKS')")
    public ApiResponse<BookResponse> updateBook(@Valid @RequestPart BookRequest request,
                                                @RequestPart(name = "image") MultipartFile file,
                                                @PathVariable(value = "id") Long id) throws IOException
    {
        return helper.buildResponse(HttpStatus.OK,"Updating book succeed!", bookService.handleUpdateBook(request,file, id));
    }

    @PatchMapping("/books/update/{id}/stock")
    @PreAuthorize("hasAuthority('EDIT_BOOKS')")
    public ApiResponse<BookResponse> updateStock(@PathVariable(value = "id") Long id,
                                                 @RequestBody @Valid UpdateStockRequest request)
    {
        return helper.buildResponse(HttpStatus.OK,"Updating book's stock succeed!", bookService.handleUpdateBookStock(id, request));
    }

    @PatchMapping("/books/update/{id}/price")
    @PreAuthorize("hasAuthority('EDIT_BOOKS')")
    public ApiResponse<BookResponse> updatePrice(@PathVariable(value = "id") Long id,
                                                 @RequestBody @Valid UpdatePriceRequest request)
    {
        return helper.buildResponse(HttpStatus.OK,"Updating book's price succeed!", bookService.handleUpdateBookPrice(id, request));
    }

    @PatchMapping("/books/update/{id}/image")
    @PreAuthorize("hasAuthority('EDIT_BOOKS')")
    public ApiResponse<BookResponse> updateImage(@PathVariable(value = "id") Long id,
                                                 @RequestParam("file") MultipartFile file) throws IOException {
        return helper.buildResponse(HttpStatus.OK,"Updating book's image succeed!", bookService.handleUpdateBookImage(id, file));
    }

    @PatchMapping("/books/update/{id}/info")
    @PreAuthorize("hasAuthority('EDIT_BOOKS')")
    public ApiResponse<BookResponse> updateImage(@PathVariable(value = "id") Long id,
                                                 @RequestBody @Valid UpdateBookInfoRequest request) {
        return helper.buildResponse(HttpStatus.OK,"Updating book's info succeed!", bookService.handleUpdateBookInfo(id, request));
    }


    @DeleteMapping("/books/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_BOOKS')")
    public ApiResponse<Void> deleteBook(@PathVariable(value = "id") Long id)
    {
        bookService.handleDeleteBook(id);
        return helper.buildResponse(HttpStatus.OK,"Deleting book succeed!", null);
    }

    @PostMapping("/books/{id}/rate")
    @PreAuthorize("hasAuthority('RATE_BOOK')")
    public ApiResponse<Void> rateBook(@PathVariable(value = "id") Long id,
                                      @RequestBody @Valid RatingRequest request)
    {
        bookService.handleRateBook(id, request);
        return helper.buildResponse(HttpStatus.OK,"Deleting book succeed!", null);
    }
}
