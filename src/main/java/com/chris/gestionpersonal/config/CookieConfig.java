package com.chris.gestionpersonal.config;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieConfig {

    @Value("${jwt.cookie.secure:false}")
    private boolean jwtCookieSecure;

    @Value("${jwt.cookie.same-site:Lax}")
    private String jwtCookieSameSite;

    public Cookie buildJwtCookie(String jwt, int maxAgeSeconds) {
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtCookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setAttribute("SameSite", jwtCookieSameSite);
        return cookie;
    }

    public Cookie buildClearedJwtCookie() {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtCookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", jwtCookieSameSite);
        return cookie;
    }
}
