package com.example.pixeltest.Services;

import com.example.pixeltest.Services.DTOs.UserDTO;
import com.example.pixeltest.DAL.Repositories.UserRepository;
import com.example.pixeltest.DAL.Utils.UserMapper;
import com.example.pixeltest.DAL.models.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        return UserMapper.toDto(user);
    }

}