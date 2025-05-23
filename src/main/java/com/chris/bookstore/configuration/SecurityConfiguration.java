package com.chris.bookstore.configuration;

import com.chris.bookstore.dto.response.ApiResponse;
import com.chris.bookstore.enums.Privilege;
import com.chris.bookstore.util.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Value("${jwt.base64-secret}")
    private String jwtKey;

    private final String[] PUBLIC_ENDPOINTS = {
            "/auth/login",
            "/auth/logout",
            "/auth/refresh",
            "/auth/sign-up",
            "/auth/verify-email",
            "/books/**",
            "/categories/**",
    };

    private final String[] USER_ENDPOINTS = {
            "/users/**",
    };

    private final String[] ADMIN_ENDPOINTS = {
            "/admin/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           CustomAuthenticationEntryPoint caep) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(USER_ENDPOINTS).hasAuthority("ROLE_USER")
                        .requestMatchers(ADMIN_ENDPOINTS).hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) //401
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) //403
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .anonymous(AbstractHttpConfigurer::disable) // Disable anonymous authentication
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(caep));
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();

        return token -> {
            Jwt jwt = jwtDecoder.decode(token);
            var expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && Instant.now().isAfter(expiresAt)) {
                // Wrap JwtExpiredException in AuthenticationServiceException
                throw new RuntimeException("Jwt expired");
            }
            return jwt;
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow only requests from your frontend (adjust as needed for production)
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));

        // Allow any headers
        configuration.addAllowedHeader("*");

        // Allow any HTTP method (GET, POST, etc.)
        configuration.addAllowedMethod("*");

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true); // This is crucial for enabling credentials!

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        // Apply the CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            String role = jwt.getClaim("role");
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority(role)); // Convert to Spring Security format
            }

            List<String> privileges = jwt.getClaim("privileges");
            if (privileges != null) {
                for (String privilege : privileges) {
                    try {
                        Privilege enumPrivilege = Privilege.valueOf(privilege);
                        authorities.add(new SimpleGrantedAuthority(enumPrivilege.name()));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid privilege: " + privilege);
                    }
                }
            }
            return authorities;
        });
        return jwtAuthenticationConverter;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
