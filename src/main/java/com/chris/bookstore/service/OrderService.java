package com.chris.bookstore.service;

import com.chris.bookstore.dto.response.OrderResponse;
import com.chris.bookstore.entity.*;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.enums.OrderStatus;
import com.chris.bookstore.enums.Role;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final CartItemsRepository cartItemsRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository,
                        AddressRepository addressRepository,
                        UserService userService,
                        CartItemsRepository cartItemsRepository,
                        CartRepository cartRepository)
    {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.userService =  userService;
        this.cartItemsRepository = cartItemsRepository;
        this.cartRepository = cartRepository;
    }

    public List<OrderResponse> getAllOrders() {
        User currentUser = this.userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();

        List<Order> orders = this.orderRepository.getOrdersByShopId(currentShop.getId());

        return orders.stream().map(OrderResponse::new).toList();
    }

    @Transactional
    public void placeAnOrder(Long addressId) {
        User currentUser = this.userService.getCurrentUser();
        Address existingAddress = this.addressRepository.findByUserIdAndId(currentUser.getId(), addressId);
        if (existingAddress == null)
            throw new AppException(ErrorCode.ADDRESS_NOT_EXISTED);

        Cart currentUserCart = currentUser.getCart();
        if (currentUserCart == null || currentUserCart.getCartItems().isEmpty())
            throw new AppException(ErrorCode.CART_EMPTY);

        // Group cart items by shop
        Map<Shop, List<CartItems>> itemsByShop = currentUserCart.getCartItems().stream()
                .collect(Collectors.groupingBy(
                        cartItem -> cartItem.getBook().getShop()
                ));

        // Create separate orders for each shop
        List<Order> orders = new ArrayList<>();

        for (Map.Entry<Shop, List<CartItems>> entry : itemsByShop.entrySet()) {
            Shop shop = entry.getKey();
            List<CartItems> shopItems = entry.getValue();

            Order newOrder = new Order();
            newOrder.setUser(currentUser);
            newOrder.setStatus(OrderStatus.PENDING);
            newOrder.setShippingAddress(existingAddress.toString());
            newOrder.setShop(shop);

            // Add order details for this shop's items
            double shopTotal = 0.0;
            for (CartItems item : shopItems) {

                if (item.getBook().getStock() < item.getQuantity()) {
                    throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
                }

                Book book = item.getBook();
                book.setStock(book.getStock() - item.getQuantity());

                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setOrder(newOrder);
                orderDetails.setBook(item.getBook());
                orderDetails.setQuantity(item.getQuantity());
                orderDetails.setUnitPrice(item.getUnitPrice());

                shopTotal += item.getUnitPrice() * item.getQuantity();
                newOrder.getOrderDetails().add(orderDetails);
            }

            newOrder.setTotalAmount(shopTotal);
            orders.add(newOrder);
        }

        // Clear the cart and save changes
        currentUserCart.clearCart(cartItemsRepository);
        cartRepository.save(currentUserCart);

        // Save all orders
        orderRepository.saveAll(orders);
    }

    public void updateStatus(Long order, OrderStatus newStatus) {
        Order existingOrder = this.orderRepository.findById(order)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        User currentUser = this.userService.getCurrentUser();
        boolean isNotCurrentShopOrder = this.orderRepository.getOrderByIdAndShopId(order, currentUser.getShop().getId()) == null;
        if (isNotCurrentShopOrder && currentUser.getRole() != Role.ADMIN)
            throw new AppException(ErrorCode.ORDER_NOT_EXISTED);

        OrderStatus currentStatus = existingOrder.getStatus();

        switch (newStatus) {
            case CONFIRMED:
                if (currentStatus != OrderStatus.PENDING)
                    throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
                break;
            case DELIVERED:
                if (currentStatus != OrderStatus.CONFIRMED)
                    throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
                break;
            case SHIPPED:
                if (currentStatus != OrderStatus.SHIPPED)
                    throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
                break;
            case CANCELLED:
                if (currentStatus == OrderStatus.SHIPPED)
                    throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
                break;
            default:
                throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
        }

        existingOrder.setStatus(newStatus);
        orderRepository.save(existingOrder);
    }

}
