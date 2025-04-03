package com.chris.bookstore.util;

import com.chris.bookstore.dto.request.AuthenticationRequest;
import com.chris.bookstore.dto.response.AuthenticationResponse;
import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.ErrorCode;
import com.chris.bookstore.exception.AppException;
import com.chris.bookstore.service.UserService;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
public class SecurityUtil {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    public SecurityUtil(JwtEncoder jwtEncoder,
                        UserService userService){
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    @Value("${jwt.valid-duration-in-seconds}")
    private String jwtKeyExpriration;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private String refreshTokenValidity;

    @Value("${jwt.base64-secret}")
    private String jwtKey;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    public  String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add("ROLE_" + user.getRole().name());
        return  stringJoiner.toString();
    }

    public String createToken(String username,
                              AuthenticationResponse.UserLogin dto, boolean isRefresh){
        Instant now = Instant.now();
        Instant validity;
        if(!isRefresh)  validity = now.plus(Long.parseLong(this.jwtKeyExpriration), ChronoUnit.SECONDS);
        else validity = now.plus(Long.parseLong(this.refreshTokenValidity), ChronoUnit.SECONDS);

        User currentUser = this.userService.getUserByUsername(username);

        String role = buildScope(this.userService.getUserByUsername(username));
        List<String> privileges = currentUser.getPrivileges().stream()
                .map(Enum::name) // Convert enum to String
                .toList();


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(username)
                .claim("user", dto)
                .claim("role",role)
                .claim("privileges",privileges)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String getCurrentUserJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String username = jwt.getSubject();
            return username;
        }
        return null;
    }

    public Jwt checkTokenValidation(String token)
    {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(JWT_ALGORITHM).build();
        try
        {
            return jwtDecoder.decode(token);
        } catch (Exception ex)
        {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    public SecretKey getSecretKey(){
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0 , keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

}
