package com.example.pixeltest.API;

import com.example.pixeltest.JWT.JwtUtils;
import com.example.pixeltest.JWT.LoginRequest;
import com.example.pixeltest.Models.DTOs.UserDTO;
import com.example.pixeltest.Models.Ntities.User;
import com.example.pixeltest.Services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public AuthController(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        logger.info("Authenticating user: {}", loginRequest.getName());
        try {
            User user = userService.getUserByName(loginRequest.getName());
            if (user == null) {
                return ResponseEntity.status(401).body(Collections.singletonMap("error", "User not found"));
            }
            if (user.getPassword() == null || !user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid credentials"));
            }

            String token = jwtUtils.generateToken(user.getId());

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);

            response.addCookie(cookie);

            Map<String, Object> body = new HashMap<>();
            body.put("userId", user.getId());
            body.put("username", user.getName());

            return ResponseEntity.ok(body);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        logger.info("Creating user with name: {}", userDTO.getName());

        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().build();
        }

        try {
            UserDTO createdUser = userService.createUser(userDTO);
            logger.info("Successfully created user with name: {}", userDTO.getName());
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
