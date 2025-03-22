package com.chris.bookstore.service;

import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username){
        return this.userRepository.findByUsername(username);
    }

    public void updateUserToken(String token, String username)
    {
        User user = this.userRepository.findByUsername(username);
        if(user != null)
        {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndUsername(String token, String username)
    {
        User user = this.userRepository.findByRefreshTokenAndUsername(token,username);
        if(user ==null)
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        return user;
    }
}
