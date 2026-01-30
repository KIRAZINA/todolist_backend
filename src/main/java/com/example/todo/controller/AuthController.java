package com.example.todo.controller;

import com.example.todo.dto.user.UserLoginRequest;
import com.example.todo.dto.user.UserRegisterRequest;
import com.example.todo.dto.user.UserResponse;
import com.example.todo.security.JwtTokenProvider;
import com.example.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User registration and login")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Register a new user", description = "Creates a new user with role USER")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/register")
    public com.example.todo.dto.common.ApiResponse<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());
        UserResponse response = userService.register(request);
        log.info("User registered successfully: {}", request.getUsername());
        return com.example.todo.dto.common.ApiResponse.created(response, "User registered successfully");
    }

    @Operation(summary = "Login user", description = "Returns JWT token")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public com.example.todo.dto.common.ApiResponse<Map<String, String>> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            log.info("Login attempt for username: {}", request.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtTokenProvider.generateToken(authentication);
            Map<String, String> data = Map.of("token", jwt, "type", "Bearer");

            log.info("Login successful for username: {}", request.getUsername());
            return com.example.todo.dto.common.ApiResponse.success(data, "Login successful");
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for username: {}", request.getUsername());
            throw e;
        }
    }
}
