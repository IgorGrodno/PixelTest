package com.example.pixeltest.Models.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private BigDecimal balance;
    private Set<String> emails;
    private Set<String> phones;

    public UserDTO(Long id, String name, LocalDate birthDate, BigDecimal balance,
                   Set<String> emails, Set<String> phones) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.balance = balance;
        this.emails = emails;
        this.phones = phones;
    }
}

