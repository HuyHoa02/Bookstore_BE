package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.AddressRequest;
import com.chris.bookstore.dto.response.AddressResponse;
import com.chris.bookstore.entity.Address;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.AddressRepository;
import com.chris.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository,
                          UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<AddressResponse> getAll(Long userId) {
        List<Address> addresses = this.addressRepository.findAllByUserId(userId);

        return addresses.stream().map(address -> {
            AddressResponse res = new AddressResponse();
            res.setAddress(address.getAddress());
            return res;
        }).toList();
    }

    public void addAddress(AddressRequest request)
    {
        User user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Address address = new Address();
        address.setAddress(request.getAddress());
        address.setUser(user);

        addressRepository.save(address);
    }

    public void updateAddress(Long id, AddressRequest request)
    {
        Address address = this.addressRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        address.setAddress(request.getAddress());

        addressRepository.save(address);
    }

    public void deleteAddress(Long id)
    {
        Address address = this.addressRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXISTED));

        addressRepository.deleteById(id);
    }
}
