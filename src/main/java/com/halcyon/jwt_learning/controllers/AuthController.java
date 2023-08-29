package com.halcyon.jwt_learning.controllers;

import com.halcyon.jwt_learning.dto.RegisterDto;
import com.halcyon.jwt_learning.security.AuthRequest;
import com.halcyon.jwt_learning.security.AuthResponse;
import com.halcyon.jwt_learning.security.RefreshRequest;
import com.halcyon.jwt_learning.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterDto dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        AuthResponse tokens = authService.register(dto);
        return ResponseEntity.ok(tokens);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
        AuthResponse tokens = authService.login(authRequest);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/access")
    public ResponseEntity<AuthResponse> getAccessToken(@RequestBody RefreshRequest request) {
        AuthResponse tokens = authService.getTokensByRefresh(request.getRefreshToken(), false);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> getRefreshToken(@RequestBody RefreshRequest request) {
        AuthResponse tokens = authService.getTokensByRefresh(request.getRefreshToken(), true);
        return ResponseEntity.ok(tokens);
    }

}
