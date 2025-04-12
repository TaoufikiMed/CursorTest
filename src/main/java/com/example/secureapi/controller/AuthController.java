package com.example.secureapi.controller;

import com.example.secureapi.dto.ApiResponse;
import com.example.secureapi.dto.JwtAuthenticationResponse;
import com.example.secureapi.dto.LoginRequest;
import com.example.secureapi.dto.SignupRequest;
import com.example.secureapi.exception.AuthenticationException;
import com.example.secureapi.model.Role;
import com.example.secureapi.model.User;
import com.example.secureapi.repository.RoleRepository;
import com.example.secureapi.repository.UserRepository;
import com.example.secureapi.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            return ResponseEntity.ok(ApiResponse.success(
                "Login successful",
                new JwtAuthenticationResponse(jwt)
            ));
        } catch (Exception e) {
            log.error("Error during authentication", e);
            throw new AuthenticationException("Invalid email or password");
        }
    }

    @Operation(summary = "Register a new user account")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                throw new AuthenticationException("Email is already in use!");
            }

            User user = new User();
            user.setEmail(signUpRequest.getEmail());
            user.setUsername(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setFullName(signUpRequest.getFullName());
            user.setActive(true);
            
            log.debug("Looking up ROLE_USER...");
            Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new AuthenticationException("Default role not found"));
            log.debug("Found role: {}", userRole);
            
            user.setRoles(Collections.singleton(userRole));
            log.debug("Assigned role to user: {}", userRole.getName());

            userRepository.save(user);
            log.info("User registered successfully: {} with role: {}", user.getEmail(), userRole.getName());
            
            return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during user registration", e);
            throw new AuthenticationException("Failed to register user", e);
        }
    }
} 