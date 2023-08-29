package com.halcyon.jwt_learning.services.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtProvider {

    private final SecretKey accessSecret;
    private final SecretKey refreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String accessSecret,
            @Value("${jwt.secret.refresh}") String refreshSecret
    ) {
        this.accessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.refreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    public String generateToken(UserDetails userDetails, boolean isRefresh) {
        return generateToken(new HashMap<>(), userDetails, isRefresh);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, boolean isRefresh) {
        LocalDateTime now = LocalDateTime.now();

        Instant expirationInstant = isRefresh ?
                now.plusDays(31).atZone(ZoneId.systemDefault()).toInstant() :
                now.plusDays(7).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(expirationInstant))
                .signWith(isRefresh ? refreshSecret : accessSecret)
                .compact();
    }

    public Claims extractAccessClaims(String jwtToken) {
        return extractAllClaims(jwtToken, accessSecret);
    }

    public Claims extractRefreshClaims(String jwtToken) {
        return extractAllClaims(jwtToken, refreshSecret);
    }

    private Claims extractAllClaims(String jwtToken, Key secret) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver, Key secret) {
        Claims claims = extractAllClaims(jwtToken, secret);

        return claimsResolver.apply(claims);
    }

    public boolean isValidAccessToken(String accessToken) {
        return isValidToken(accessToken, accessSecret);
    }

    public boolean isValidRefreshToken(String refreshToken) {
        return isValidToken(refreshToken, refreshSecret);
    }

    private boolean isValidToken(String token, Key secret) {
        return true;
    }

}
