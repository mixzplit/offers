package com.tupperware.auth.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
	
	
	private final Key key;
	
	@Value("${jwt.expirationMs}")
	private Long jwtExpiration;
	
	
	public JwtUtil(@Value("${jwt.secret}") String secretKet) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKet));
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	private Claims extractAllClaims(String token) {
		 return Jwts.parser()
				 	.verifyWith((SecretKey) key)
				 	.build()
				 	.parseSignedClaims(token)
				 .getPayload();
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	// Generar un token para el usuario (subject)
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
	
	// Crear el token JWT con claims y subject
	private String createToken(Map<String, Object> claims, String subject)  {
		logger.info("Creando token en JwtUtil class...");
		return Jwts.builder()
					.claims(claims)
					.subject(subject)
					.issuedAt(new Date(System.currentTimeMillis()))
					.expiration(new Date(System.currentTimeMillis() + jwtExpiration))// 1 dia
					.signWith(key)
					.compact();
	}
	
	// Extraer la fecha de expiración del JWT
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
	
	// Verificar si el JWT ha expirado
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    
    // Validar el token
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
	
	
