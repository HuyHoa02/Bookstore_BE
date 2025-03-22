package com.chris.bookstore.service;

import com.chris.bookstore.entity.Address;
import com.chris.bookstore.entity.Cart;
import com.chris.bookstore.entity.Order;
import com.chris.bookstore.entity.OrderDetails;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.enums.OrderStatus;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.AddressRepository;
import com.chris.bookstore.repository.CartRepository;
import com.chris.bookstore.repository.OrderDetailsRepository;
import com.chris.bookstore.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        OrderDetailsRepository orderDetailsRepository,
                        AddressRepository addressRepository)
    {
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
    }

    public void placeAnOrder(Long cartId, Long AddressId){
        Cart existingCart = this.cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        Address existingAddress = this.addressRepository.findByUserIdAndId(existingCart.getUser().getId(), AddressId);

        if(existingCart.getTotalAmount() == 0)
            throw new AppException(ErrorCode.CART_EMPTY);

        Order newOrder = new Order();
        List<OrderDetails> detailsList = existingCart.getCartItems().stream().map(item -> {
            OrderDetails newDetails = new OrderDetails();
            newDetails.setOrder(newOrder);
            newDetails.setBook(item.getBook());
            newDetails.setQuantity(item.getQuantity());
            newDetails.setUnitPrice(item.getUnitPrice());

            return newDetails;
        }).toList();
        newOrder.setOrderDetails(detailsList);
        newOrder.setTotalAmount(existingCart.getTotalAmount());
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setUser(existingCart.getUser());
        newOrder.setShippingAddress(existingAddress.getAddress());

        cartRepository.deleteById(cartId);

        orderRepository.save(newOrder);
    }

    public void updateStatus(Long order, int option) {
        Order existingOrder = this.orderRepository.findById(order)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        int currentStatus = existingOrder.getStatus().getCode();
        OrderStatus newStatus;

        switch (option) {
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
