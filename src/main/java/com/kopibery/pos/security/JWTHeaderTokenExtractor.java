package com.kopibery.pos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;


@Component
public class JWTHeaderTokenExtractor implements TokenExtractor {

	private static final String HEADER_PREFIX = "Bearer ";
	private final SecretKey key;

    public JWTHeaderTokenExtractor(SecretKey key) {
        this.key = key;
    }

    @Override
	public String extract(String payload) {
		if (payload != null && payload.startsWith("Bearer ")) {
			return payload.substring(7); // Extract the token
		}
		return null;
	}

	@Override
	public String getEmail(String token) {
		try{
			Claims claims = Jwts.parser().verifyWith(key)
					.build().parseSignedClaims(token).getPayload();
			return claims.getSubject();
		} catch (JwtException | IllegalArgumentException e){
			// Log the exception for debugging purposes
			System.err.println("Error extracting email from token: " + e.getMessage());
			return null;
		}
	}

	@Override
	public Long getUserId (String token) {
		try{
			Claims claims = Jwts.parser().verifyWith(key)
					.build().parseSignedClaims(token).getPayload();
			return claims.get("x", Long.class);
		} catch (JwtException | IllegalArgumentException e){
			// Log the exception for debugging purposes
			System.err.println("Error extracting userId from token: " + e.getMessage());
			return null;
		}
	}

	@Override
	public Claims getClaimsFromToken(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

}