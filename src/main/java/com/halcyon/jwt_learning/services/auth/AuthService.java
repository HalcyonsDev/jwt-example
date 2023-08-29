package com.halcyon.jwt_learning.services.auth;

import com.halcyon.jwt_learning.dto.RegisterDto;
import com.halcyon.jwt_learning.models.Token;
import com.halcyon.jwt_learning.models.User;
import com.halcyon.jwt_learning.repositories.ITokensRepository;
import com.halcyon.jwt_learning.security.AuthRequest;
import com.halcyon.jwt_learning.security.AuthResponse;
import com.halcyon.jwt_learning.security.JwtAuthentication;
import com.halcyon.jwt_learning.services.security.AppUserDetailsService;
import com.halcyon.jwt_learning.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final ITokensRepository tokensRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterDto dto) {

        if (userService.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists!");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .build();

        userService.create(user);

        String accessToken = jwtProvider.generateToken(user, false);
        String refreshToken = jwtProvider.generateToken(user, true);

        tokensRepository.save(new Token(user, refreshToken));

        return new AuthResponse(accessToken, refreshToken);

    }

    public AuthResponse login(AuthRequest authRequest) {

        User user = userService.findByEmail(authRequest.getEmail());

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data");
        }

        String accessToken = jwtProvider.generateToken(user, false);
        String refreshToken = jwtProvider.generateToken(user, true);

        tokensRepository.save(new Token(user, refreshToken));

        return new AuthResponse(accessToken, refreshToken);

    }

    public AuthResponse getTokensByRefresh(String refreshToken, boolean isRefresh) {
        String subject = jwtProvider.extractRefreshClaims(refreshToken).getSubject();

        if (!jwtProvider.isValidRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Token token = tokensRepository.findByValue(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        if (!token.getValue().equals(refreshToken) || !token.getOwner().getEmail().equals(subject)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        User user = userService.findByEmail(subject);

        String accessToken = jwtProvider.generateToken(user, false);
        String newRefreshToken = null;

        if (isRefresh) {
            newRefreshToken = jwtProvider.generateToken(user, true);
            tokensRepository.save(new Token(user, newRefreshToken));
        }

        return new AuthResponse(accessToken, newRefreshToken);

    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

}
