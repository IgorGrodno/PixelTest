package com.example.pixeltest.API;

import com.example.pixeltest.JWT.JwtUtils;
import com.example.pixeltest.JWT.LoginRequest;
import com.example.pixeltest.Models.Ntities.User;
import com.example.pixeltest.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getName(), loginRequest.getPassword()));

            User user = userService.getUserByName(loginRequest.getName());

            String token = jwtUtils.generateToken(user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("username", user.getName());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid credentials"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Unauthorized: user not authenticated"
            ));
        }

        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "id", userId
        ));
    }

}

