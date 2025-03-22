package com.chris.bookstore.controller;

import com.chris.bookstore.dto.request.AddressRequest;
import com.chris.bookstore.dto.request.AuthenticationRequest;
import com.chris.bookstore.dto.request.RegisterRequest;
import com.chris.bookstore.dto.response.AddressResponse;
import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.dto.response.AuthenticationResponse;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.enums.Role;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.service.AddressService;
import com.chris.bookstore.service.AuthenticationService;
import com.chris.bookstore.service.UserService;
import com.chris.bookstore.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidity;


    public AuthenticationController(AuthenticationService authenticationService,
                                    AuthenticationManagerBuilder authenticationManagerBuilder,
                                    SecurityUtil securityUtil,
                                    UserService userService) {
        this.authenticationService = authenticationService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);


        //Convert to Response class
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        User user = userService.getUserByUsername(request.getUsername());
        AuthenticationResponse.UserLogin userLogin = null;
        if (user != null) {
            userLogin = new AuthenticationResponse.UserLogin(
                    user.getId(), user.getEmail(), user.getFullName()
            );

            authenticationResponse.setUser(userLogin);
        }


        // Create Token
        String accessToken = securityUtil.createToken(user.getUsername(), userLogin);
        String refreshToken = securityUtil.createRefreshToken(user.getUsername(), userLogin);


        authenticationResponse.setAccessToken(accessToken);
        authenticationResponse.setRefreshToken(refreshToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        userService.updateUserToken(refreshToken, request.getUsername());

        //Set cookies
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidity)
                .build();

        ApiResponse<AuthenticationResponse> res = new ApiResponse<AuthenticationResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Login successful");
        res.setResult(authenticationResponse);


        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(res);
    }

    @PostMapping("/sign-up")
    public ApiResponse<Void> signup(@Valid @RequestBody RegisterRequest request) {
        this.authenticationService.register(request, Role.USER);

        ApiResponse<Void> res = new ApiResponse<Void>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Signing up succeed");
        return res;
    }


    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "invalid_token_value") String refresh_token

    ){
        if (refresh_token.equals("invalid_token_value"))
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);

        Jwt decodedToken = this.securityUtil.checkTokenValidation(refresh_token);
        String username = decodedToken.getSubject();

        // Get current user by refresh token in cookie
        User currentUser = this.userService.getUserByRefreshTokenAndUsername(refresh_token,username);

        //Convert to Response class
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        AuthenticationResponse.UserLogin userLogin = null;
        if (currentUser != null) {
            userLogin = new AuthenticationResponse.UserLogin(
                    currentUser.getId(), currentUser.getEmail(), currentUser.getFullName()
            );

            authenticationResponse.setUser(userLogin);
        }


        // Create Token
        String accessToken = securityUtil.createToken(currentUser.getUsername(), userLogin);
        String refreshToken = securityUtil.createRefreshToken(currentUser.getUsername(), userLogin);


        authenticationResponse.setAccessToken(accessToken);
        authenticationResponse.setRefreshToken(refreshToken);

        userService.updateUserToken(refreshToken, currentUser.getUsername());

        //Set cookies
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidity)
                .build();

        ApiResponse<AuthenticationResponse> res = new ApiResponse<AuthenticationResponse>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Login successful");
        res.setResult(authenticationResponse);


        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(res);
    }

    @GetMapping("/account")
    public ApiResponse<AuthenticationResponse.UserLogin> getAccount()
    {
        String username = this.securityUtil.getCurrentUserJWT();
        User currentUser = this.userService.getUserByUsername(username);

        AuthenticationResponse.UserLogin userLogin = new AuthenticationResponse.UserLogin();
        if(currentUser != null){
            userLogin.setId(currentUser.getId());
            userLogin.setEmail(currentUser.getEmail());
            userLogin.setName(currentUser.getFullName());
        }

        ApiResponse<AuthenticationResponse.UserLogin> res = new ApiResponse<AuthenticationResponse.UserLogin>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Get current succeed");
        res.setResult(userLogin);

        return res;
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "refresh_token", defaultValue = "invalid_token_value") String refresh_token
    )
    {
        if (refresh_token.equals("invalid_token_value"))
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);

        Jwt decodedToken = this.securityUtil.checkTokenValidation(refresh_token);
        String username = decodedToken.getSubject();

        // Get current user by refresh token in cookie
        User currentUser = this.userService.getUserByRefreshTokenAndUsername(refresh_token,username);

        this.userService.updateUserToken(null,username);

        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(null);
    }
}
