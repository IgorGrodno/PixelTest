package com.example.pixeltest.Models.DTOs;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Min(value = 0, message = "Balance must be a positive number")
    private BigDecimal balance;

    private String password;

    @NotNull
    @Size(min = 1, message = "At least one phone number is required")
    private Set<String> phones;

    @NotNull
    @Size(min = 1, message = "At least one email is required")
    private Set<String> emails;

    public UserDTO(Long id, String name, LocalDate birthDate, BigDecimal balance,
                   Set<String> emails, Set<String> phones, String password) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.balance = balance;
        this.emails = emails;
        this.phones = phones;
        this.password = password;
    }

    public UserDTO(Long id, String name, LocalDate birthDate, BigDecimal balance,
                   Set<String> emails, Set<String> phones) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.balance = balance;
        this.emails = emails;
        this.phones = phones;
    }

    public UserDTO() {
    }
}

