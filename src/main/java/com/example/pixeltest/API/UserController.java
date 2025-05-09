package com.example.pixeltest.API;

import com.example.pixeltest.Models.DTOs.UserDTO;
import com.example.pixeltest.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>>  getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDTOById(id));
    }

    @GetMapping("/search")
    public Page<UserDTO> searchUsers(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) String phone,
                                     @RequestParam(required = false) String email,
                                     @RequestParam(required = false) Date dateOfBirth,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return userService.searchUsers(name, phone, email, dateOfBirth, page, size);
    }

}

