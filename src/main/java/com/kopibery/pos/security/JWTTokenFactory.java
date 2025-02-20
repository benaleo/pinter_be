package com.kopibery.pos.security;

import com.kopibery.pos.entity.Users;
import com.kopibery.pos.repository.UserRepository;
import com.kopibery.pos.security.model.AccessJWTToken;
import com.kopibery.pos.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@RequiredArgsConstructor
public class JWTTokenFactory {

    private final Key secret;
    private final UserService userService;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public AccessJWTToken createAccessJWTToken(String email, Collection<? extends GrantedAuthority> authorities) {
        Claims claims;
        Users user = userService.findByEmail(email);
        claims = Jwts.claims().subject(email)
                .add("x", user.getId())
                .add("scopes", Arrays.asList(user.getRole().getName())).build();

        //created time
        LocalDateTime currentTime = LocalDateTime.now();
        Date currentTimeDate = Date.from(currentTime.atZone(ZoneId.of("Asia/Jakarta")).toInstant());

        //expired time
        LocalDateTime expiredTime = currentTime.plusSeconds((jwtExpirationInMs / 1000) * 7);
        Date expiredTimeDate = Date.from(expiredTime.atZone(ZoneId.of("Asia/Jakarta")).toInstant());

        String token = Jwts.builder().claims(claims)
                .issuer("https://kasirpinter.id")
                .issuedAt(currentTimeDate)
                .expiration(expiredTimeDate)
                .signWith(secret).compact();

        return new AccessJWTToken(token, claims);
    }

    public long getExpirationTime() {
        return jwtExpirationInMs;
    }


    public void invalidateToken(String token) {
        try {
            // Parse and verify the token (optional, for extra validation)
            Jwts.parser()
                    .verifyWith((SecretKey) secret)
                    .build()
                    .parseSignedClaims(token);

            // Add the token to the blacklist
            blacklistToken(token);
        } catch (Exception e) {
            // Handle token parsing exceptions (e.g., expired, invalid token)
            throw new RuntimeException("Invalid token", e);
        }
    }

    private void blacklistToken(String token) {
        System.out.println("Token: " + token);
    }
}
