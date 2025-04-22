package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.AuthenticationRequest;
import com.chris.bookstore.dto.request.EmailVerifyRequest;
import com.chris.bookstore.dto.request.RegisterRequest;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.AuthenticationResponse;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.enums.Role;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.service.AuthenticationService;
import com.chris.bookstore.service.MailService;
import com.chris.bookstore.service.UserService;
import com.chris.bookstore.util.Helper;
import com.chris.bookstore.util.SecurityUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final MailService mailService;
    private final Helper helper;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidity;

    public AuthenticationController(AuthenticationService authenticationService,
                                    AuthenticationManagerBuilder authenticationManagerBuilder,
                                    SecurityUtil securityUtil,
                                    UserService userService,
                                    MailService mailService,
                                    Helper helper) {
        this.authenticationService = authenticationService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.mailService = mailService;
        this.helper = helper;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticateUser(request.getUsername(), request.getPassword());
            User user = userService.getUserByUsername(request.getUsername());

            AuthenticationResponse authenticationResponse = generateAuthResponse(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            userService.updateUserToken(authenticationResponse.getRefreshToken(), request.getUsername());

            ResponseCookie resCookie = createRefreshTokenCookie(authenticationResponse.getRefreshToken());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                    .body(helper.buildResponse(HttpStatus.OK, "Login successful", authenticationResponse));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(helper.buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", null));
        }
    }
    @PostMapping("/sign-up")
    public ApiResponse<Void> signup(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        authenticationService.register(request, Role.USER);
        return helper.buildResponse(HttpStatus.OK, "Signing up succeeded", null);
    }
    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "invalid_token_value") String refreshToken) {

        if ("invalid_token_value".equals(refreshToken))
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);

        Jwt decodedToken = securityUtil.checkTokenValidation(refreshToken);
        String username = decodedToken.getSubject();

        User currentUser = userService.getUserByRefreshTokenAndUsername(refreshToken, username);

        AuthenticationResponse authenticationResponse = generateAuthResponse(currentUser);
        userService.updateUserToken(authenticationResponse.getRefreshToken(), currentUser.getUsername());

        ResponseCookie resCookie = createRefreshTokenCookie(authenticationResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(helper.buildResponse(HttpStatus.OK, "Token refreshed successfully", authenticationResponse));
    }

    @GetMapping("/account")
    public ApiResponse<AuthenticationResponse.UserLogin> getAccount() {
        User currentUser = userService.getCurrentUser();
        AuthenticationResponse.UserLogin userLogin = new AuthenticationResponse.UserLogin();

        if (currentUser != null) {
            userLogin.setId(currentUser.getId());
            userLogin.setEmail(currentUser.getEmail());
            userLogin.setName(currentUser.getFullName());
        }

        return helper.buildResponse(HttpStatus.OK, "Fetched current user successfully", userLogin);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "refresh_token", defaultValue = "invalid_token_value") String refreshToken) {

        if ("invalid_token_value".equals(refreshToken))
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);

        Jwt decodedToken = securityUtil.checkTokenValidation(refreshToken);
        String username = decodedToken.getSubject();

        userService.updateUserToken(null, username);

        ResponseCookie resCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(helper.buildResponse(HttpStatus.OK, "Logout successful", null));
    }

    @PostMapping("/verify-email")
    public ApiResponse<String> verifyEmail(@RequestBody EmailVerifyRequest request) {
        mailService.verifyEmail(request);
        return helper.buildResponse(HttpStatus.OK, "Email verification successful", null);
    }

    /*** Helper Methods ***/

    private Authentication authenticateUser(String username, String password) {
        return authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
    }

    private AuthenticationResponse generateAuthResponse(User user) {
        AuthenticationResponse.UserLogin userLogin = new AuthenticationResponse.UserLogin(
                user.getId(), user.getEmail(), user.getFullName());

        return new AuthenticationResponse(
                securityUtil.createToken(user.getUsername(), userLogin, false),
                securityUtil.createToken(user.getUsername(), userLogin, true),
                userLogin);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidity)
                .build();
    }
}
