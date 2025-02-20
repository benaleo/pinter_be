package com.kopibery.pos.security;

import io.jsonwebtoken.Claims;

public interface TokenExtractor {
	
	String extract(String payload);

    String extractOnParam(String payload);

    String getEmail(String token);

	Long getUserId(String token);

	Claims getClaimsFromToken(String token);

}
