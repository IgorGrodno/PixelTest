package com.example.pixeltest.JWT;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String name;
    private String password;
}
