package com.example.pixeltest.JWT;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String password;

    public LoginRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public LoginRequest() {
    }
}
