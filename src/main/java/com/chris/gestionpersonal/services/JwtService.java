package com.chris.gestionpersonal.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    @Value("${jwt.expiration.minutes}")
    private Long EXPIRATION_IN_MINUTES;
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    public String generateToken(UserDetails employee, Map<String, Object> extraClaims) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date((EXPIRATION_IN_MINUTES * 60 * 1000) + issuedAt.getTime());
        return Jwts.builder()
                .header()
                .type("jwt")
                .and()
                .subject(employee.getUsername())
                .claims(extraClaims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(generateKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey generateKey() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    private Claims extractAllClaims(String jwt){
        return Jwts.parser().verifyWith(generateKey()).build()
                .parseSignedClaims(jwt).getPayload();
    }

    public String extractEmail(String jwt){
        return extractAllClaims(jwt).getSubject();
    }

    public Date extractExpiration(String jwt){
        return extractAllClaims(jwt).getExpiration();
    }


//    public String extractTokenFromRequest(HttpServletRequest request) {
//        String authorizationHeader = request.getHeader("Authorization");
//        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
//            return null;
//        }
//        return authorizationHeader.substring(7);
//    }

    public Optional<String> extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return cookies == null ? Optional.empty() :
               Arrays.stream(cookies)
                     .filter(cookie -> "jwt".equals(cookie.getName()))
                     .map(Cookie::getValue)
                     .findFirst();
    }
}
