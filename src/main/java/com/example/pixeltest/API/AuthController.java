package com.example.pixeltest.API;

import com.example.pixeltest.JWT.JwtUtils;
import com.example.pixeltest.JWT.LoginRequest;
import com.example.pixeltest.Models.Ntities.User;
import com.example.pixeltest.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getName());
        try {
            User user = userService.getUserByName(loginRequest.getName());
            if (user == null) {
                return ResponseEntity.status(401).body(Collections.singletonMap("error", "User not found"));
            }
            if(user.getPassword() == null || !user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid credentials"));
            }
            String token = jwtUtils.generateToken(user.getId());

            // Подготовка ответа с токеном
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("username", user.getName());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid credentials"));
        }
    }
}
