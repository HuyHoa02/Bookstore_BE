package com.chris.bookstore.service;

import com.chris.bookstore.entity.*;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.enums.OrderStatus;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        OrderDetailsRepository orderDetailsRepository,
                        AddressRepository addressRepository)
    {
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public void placeAnOrder(Long userId, Long AddressId){
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Address existingAddress = this.addressRepository.findByUserIdAndId(user.getId(), AddressId);

        Cart currentUserCart = user.getCart();

        Order newOrder = new Order();
        List<OrderDetails> detailsList = currentUserCart.getCartItems().stream().map(item -> {
            OrderDetails newDetails = new OrderDetails();
            newDetails.setOrder(newOrder);
            newDetails.setBook(item.getBook());
            newDetails.setQuantity(item.getQuantity());
            newDetails.setUnitPrice(item.getUnitPrice());

            return newDetails;
        }).toList();
        newOrder.setOrderDetails(detailsList);
        newOrder.setTotalAmount(currentUserCart.getTotalAmount());
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setUser(user);
        newOrder.setShippingAddress(existingAddress.toString());

        currentUserCart.clearCart();

        orderRepository.save(newOrder);
    }

    public void updateStatus(Long order, OrderStatus option) {
        Order existingOrder = this.orderRepository.findById(order)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        int currentStatus = existingOrder.getStatus().getCode();
        OrderStatus newStatus;

        switch (option.getCode()) {
            case 1:
                if (currentStatus == 0) {
                    newStatus = OrderStatus.CONFIRMED;
                } else {
                    throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
                }
                break;
            case 2:
                if (currentStatus == 1) {
                    newStatus = OrderStatus.DELIVERED;
                } else {
                    throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
                }
                break;
            case 3:
                if (currentStatus == 2) {
                    newStatus = OrderStatus.SHIPPED;
                } else {
                    throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
                }
                break;
            case 4:
                if (currentStatus >= 0 && currentStatus < 4) {
                    newStatus = OrderStatus.CANCELLED;
                } else {
                    throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
                }
                break;
            default:
                throw new AppException(ErrorCode.STATUS_OPTION_INVALID);
        }

        existingOrder.setStatus(newStatus);
        orderRepository.save(existingOrder);
    }

}
