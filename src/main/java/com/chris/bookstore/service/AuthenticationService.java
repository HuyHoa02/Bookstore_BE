package com.chris.bookstore.service;

import com.chris.bookstore.dto.DataMailDTO;
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
import com.chris.bookstore.util.Helper;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final Helper helper;
    private final MailService mailService;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder  passwordEncoder,
                                 Helper helper,
                                 MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.helper = helper;
        this.mailService = mailService;
    }

    public void register(RegisterRequest request, Role role) throws MessagingException {
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

        String verifyCode;

        do {
            verifyCode = helper.generateTempPwd(6);
        } while(this.userRepository.findByVerificationCode(verifyCode) != null);


        newUser.setVerificationCode(verifyCode);
        newUser.setVerificationExpiry(LocalDateTime.now().plusHours(24));
        newUser.setVerified(false);


        Map<String,Object> props = new HashMap<>();
        props.put("fullName",request.getFullName());
        props.put("code",verifyCode);

        DataMailDTO dataMailDTO = new DataMailDTO();
        dataMailDTO.setSubject("XÁC NHẬN TẠO MỚI THÔNG TIN NGƯỜI DÙNG");
        dataMailDTO.setTo(request.getEmail());
        dataMailDTO.setProps(props);

        this.mailService.sendHtmlMail(dataMailDTO);

        userRepository.save(newUser);
    }
}
