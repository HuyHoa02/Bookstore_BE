package com.chris.bookstore.service;

import com.chris.bookstore.dto.request.AddressRequest;
import com.chris.bookstore.dto.request.AuthenticationRequest;
import com.chris.bookstore.dto.request.RegisterRequest;
import com.chris.bookstore.dto.response.AddressResponse;
import com.chris.bookstore.dto.response.AuthenticationResponse;
import com.chris.bookstore.entity.Address;
import com.chris.bookstore.entity.Cart;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.enums.Role;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.AddressRepository;
import com.chris.bookstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder  passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request, Role role)
    {
        User existingUserEmail = this.userRepository.findByEmail(request.getEmail());
        User existingUserUsername = this.userRepository.findByUsername(request.getEmail());

        if(existingUserUsername != null || existingUserEmail != null)
            throw new AppException(ErrorCode.USER_EXISTED);

        String encodedPassword = this.passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setFullName(request.getFullName());


        newUser.setPassword(encodedPassword);

        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setRole(role);

        Cart cart = new Cart();
        newUser.setCart(cart);

        userRepository.save(newUser);

    }
}
