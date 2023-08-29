package com.halcyon.jwt_learning.utils;

import com.halcyon.jwt_learning.security.JwtAuthentication;
import com.halcyon.jwt_learning.services.auth.JwtProvider;
import com.halcyon.jwt_learning.services.security.AppUserDetailsService;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    private final AppUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;

    public static JwtAuthentication getAuthentication(Claims claims) {
        final JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setEmail(claims.getSubject());

        return jwtAuthentication;
    }
}
