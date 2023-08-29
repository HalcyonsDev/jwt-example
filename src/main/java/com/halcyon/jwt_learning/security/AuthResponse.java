package com.halcyon.jwt_learning.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;

}
