package com.example.pixeltest.Utils;

import com.example.pixeltest.Models.DTOs.UserDTO;
import com.example.pixeltest.Models.Ntities.User;
import com.example.pixeltest.Models.Ntities.EmailData;
import com.example.pixeltest.Models.Ntities.PhoneData;

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

