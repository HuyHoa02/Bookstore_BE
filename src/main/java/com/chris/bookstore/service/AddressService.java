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
    private final UserService userService;

    public AddressService(AddressRepository addressRepository,
                          UserRepository userRepository,
                          UserService userService) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<AddressResponse> getAllAddresses() {
        User currentUser = this.userService.getCurrentUser();
        List<Address> addresses = this.addressRepository.findAllByUserId(currentUser.getId());

        return addresses.stream().map(address -> {
            AddressResponse res = new AddressResponse();
            res.setId(address.getId());
            res.setStreet(address.getWard());
            res.setWard(address.getWard());
            res.setDistrict(address.getDistrict());
            res.setProvince(address.getProvince());
            return res;
        }).toList();
    }

    public void addAddress(AddressRequest request)
    {
        User currentUser = this.userService.getCurrentUser();

        currentUser.getAddresses().add(new Address(
                request.getStreet(),
                request.getWard(),
                request.getDistrict(),
                request.getProvince(),
                currentUser));
        userRepository.save(currentUser);
    }

    public void updateAddress(Long addressId, AddressRequest request)
    {
        User currentUser = this.userService.getCurrentUser();

        Address address = this.addressRepository.findByUserIdAndId(currentUser.getId(), addressId);
        if(address == null)
            throw new AppException(ErrorCode.ADDRESS_NOT_EXISTED);

        address.setStreet(request.getStreet());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setProvince(request.getProvince());

        addressRepository.save(address);
    }

    public void deleteAddress(Long addressId)
    {
        User currentUser = this.userService.getCurrentUser();

        Address address = this.addressRepository.findByUserIdAndId(currentUser.getId(), addressId);
        if(address == null)
            throw new AppException(ErrorCode.ADDRESS_NOT_EXISTED);

        addressRepository.deleteById(addressId);
    }
}
