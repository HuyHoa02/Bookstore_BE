package com.chris.bookstore.repository;

import com.chris.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    User findByRefreshTokenAndUsername(String refreshToken, String username);
    User findByVerificationCode(String verificationCode);
}
