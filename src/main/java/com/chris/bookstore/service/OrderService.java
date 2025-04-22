package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.PlaceOrderRequest;
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

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        User currentUser = this.userService.getCurrentUser();
        Shop currentShop = currentUser.getShop();

        List<Order> orders = this.orderRepository.getOrdersByShopId(currentShop.getId());

        return orders.stream().map(OrderResponse::new).toList();
    }
    @Transactional
    public void placeOrder(PlaceOrderRequest request) {
        User currentUser = userService.getCurrentUser();
        Cart cart = currentUser.getCart();

        List<CartItems> selectedItems = cart.getCartItems().stream()
                .filter(item -> request.getCartItemIds().contains(item.getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_CART_SELECTION);
        }

        // Group by shop
        Map<Shop, List<CartItems>> groupedByShop = selectedItems.stream()
                .collect(Collectors.groupingBy(item -> item.getBook().getShop()));

        for (Map.Entry<Shop, List<CartItems>> entry : groupedByShop.entrySet()) {
            Shop shop = entry.getKey();
            List<CartItems> items = entry.getValue();

            Order order = new Order();
            order.setUser(currentUser);
            order.setShop(shop);
            order.setShippingAddress(request.getShippingAddress());
            order.setNote(request.getNote());
            order.setStatus(OrderStatus.PENDING);

            double total = items.stream()
                    .mapToDouble(i -> i.getQuantity() * i.getUnitPrice())
                    .sum();
            order.setTotalAmount(total);

            List<OrderDetails> details = new ArrayList<>();
            for (CartItems cartItem : items) {
                OrderDetails detail = new OrderDetails();
                detail.setOrder(order);
                detail.setBook(cartItem.getBook());
                detail.setQuantity(cartItem.getQuantity());
                detail.setUnitPrice(cartItem.getUnitPrice());
                details.add(detail);
            }

            order.setOrderDetails(details);
            orderRepository.save(order);
        }

        cartItemsRepository.deleteAll(selectedItems);
        cart.getCartItems().removeIf(item -> request.getCartItemIds().contains(item.getId()));

        cart.setTotalAmount(
                cart.getCartItems().stream()
                        .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                        .sum()
        );
        cartRepository.save(cart);
    }


    public void handleUpdateStatus(Long orderId, OrderStatus newStatus) {
        Order currentOrder = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        User currentUser = this.userService.getCurrentUser();
        boolean isNotCurrentShopOrder = this.orderRepository.getOrderByIdAndShopId(orderId, currentUser.getShop().getId()) == null;
        if (isNotCurrentShopOrder && currentUser.getRole() != Role.ADMIN)
            throw new AppException(ErrorCode.ORDER_NOT_EXISTED);

        OrderStatus currentStatus = currentOrder.getStatus();

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

        currentOrder.setStatus(newStatus);
        orderRepository.save(currentOrder);
    }

}
