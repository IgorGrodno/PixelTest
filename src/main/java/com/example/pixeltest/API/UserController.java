package com.example.pixeltest.API;

import com.example.pixeltest.Models.DTOs.UserDTO;
import com.example.pixeltest.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        logger.info("Fetching all users");
        List<UserDTO> users = userService.getAllUsersDTO();
        logger.info("Successfully fetched {} users", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.info("Fetching user by ID: {}", id);
        UserDTO userDTO = userService.getUserDTOById(id);
        logger.info("Successfully fetched user with ID: {}", id);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        logger.info("Creating user with name: {}", userDTO.getName());
        UserDTO createdUser = userService.createUser(userDTO);
        logger.info("Successfully created user with name: {}", userDTO.getName());
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        logger.info("Successfully updated user with ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<Void> changeUserBalance(@PathVariable Long id, @RequestParam BigDecimal amount) {
        logger.info("Changing balance for user ID: {} by amount: {}", id, amount);
        userService.changeUserBalance(id, amount);
        logger.info("Successfully changed balance for user ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> sendMoney(@RequestParam Long senderId,
                                          @RequestParam Long receiverId,
                                          @RequestParam BigDecimal amount) {
        logger.info("Initiating transfer from user ID: {} to user ID: {} with amount: {}", senderId, receiverId, amount);
        userService.sendMoney(senderId, receiverId, amount);
        logger.info("Successfully completed transfer from user ID: {} to user ID: {}", senderId, receiverId);
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

        logger.info("Searching users with parameters - name: {}, phone: {}, email: {}, dateOfBirthMillis: {}, page: {}, size: {}",
                name, phone, email, dateOfBirthMillis, page, size);

        Date dateOfBirth = dateOfBirthMillis != null ? new Date(dateOfBirthMillis) : null;
        Page<UserDTO> userPage = userService.searchUsers(name, phone, email, dateOfBirth, page, size);

        logger.info("Successfully fetched {} users matching the search criteria", userPage.getTotalElements());
        return ResponseEntity.ok(userPage);
    }
}
