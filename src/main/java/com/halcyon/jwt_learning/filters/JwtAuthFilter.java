package com.halcyon.jwt_learning.filters;

import com.halcyon.jwt_learning.exception.CustomException;
import com.halcyon.jwt_learning.security.JwtAuthentication;
import com.halcyon.jwt_learning.services.auth.JwtProvider;
import com.halcyon.jwt_learning.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = getTokenFromRequest(request);

        try {
            if (jwtToken != null && jwtProvider.isValidAccessToken(jwtToken)) {
                Claims claims = jwtProvider.extractAccessClaims(jwtToken);

                JwtAuthentication jwtAuthentication = JwtUtils.getAuthentication(claims);
                jwtAuthentication.setAuthenticated(true);

                SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
            }
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();
            response.sendError(e.getHttpStatus().value(), e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;

    }
}
