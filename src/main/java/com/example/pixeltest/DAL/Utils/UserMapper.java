package com.example.pixeltest.DAL.Utils;

import com.example.pixeltest.Services.DTOs.UserDTO;
import com.example.pixeltest.DAL.models.User;
import com.example.pixeltest.DAL.models.EmailData;
import com.example.pixeltest.DAL.models.PhoneData;

import java.util.stream.Collectors;

public class UserMapper {
    public static UserDTO toDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getBirthDate(),
                user.getAccount() != null ? user.getAccount().getBalance() : null,
                user.getEmails().stream().map(EmailData::getEmail).collect(Collectors.toSet()),
                user.getPhones().stream().map(PhoneData::getPhone).collect(Collectors.toSet())
        );
    }
}

