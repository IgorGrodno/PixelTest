package com.example.pixeltest.API;

import com.example.pixeltest.Models.DTOs.UserDTO;
import com.example.pixeltest.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsersDTO();
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDTOById(id));
    }


    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }


    @PatchMapping("/{id}/balance")
    public ResponseEntity<Void> changeUserBalance(@PathVariable Long id, @RequestParam BigDecimal amount) {
        userService.changeUserBalance(id, amount);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/transfer")
    public ResponseEntity<Void> sendMoney(@RequestParam Long senderId,
                                          @RequestParam Long receiverId,
                                          @RequestParam BigDecimal amount) {
        userService.sendMoney(senderId, receiverId, amount);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long dateOfBirthMillis,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Date dateOfBirth = dateOfBirthMillis != null ? new Date(dateOfBirthMillis) : null;
        return ResponseEntity.ok(userService.searchUsers(name, phone, email, dateOfBirth, page, size));
    }
}

