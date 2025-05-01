package com.chris.gestionpersonal.config;

import com.chris.gestionpersonal.repositories.TokenRepository;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Jwt;
import com.chris.gestionpersonal.services.EmployeeService;
import com.chris.gestionpersonal.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private TokenRepository tokenRepository;
    private EmployeeService employeeService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwt = jwtService.extractJwtFromCookie(request);
        if(jwt.isEmpty()){
            filterChain.doFilter(request,response);
            return;
        }
        Optional<Jwt> findToken = tokenRepository.findByToken(jwt.get());
        boolean isValid = validateToken(findToken);
        if(!isValid){
            findToken.ifPresent(this::updateTokenStatus);
            filterChain.doFilter(request,response);
            return;
        }
        String email = jwtService.extractEmail(jwt.get());
        log.info("probando empleado encontrado");
        Employee employee = employeeService.findByEmail(email);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(email,null, employee.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request,response);

    }

    private boolean validateToken(Optional<Jwt> optionalToken) {
        if (optionalToken.isEmpty()){
            return false;
        }
        Jwt token = optionalToken.get();
        Date now = new Date((System.currentTimeMillis()));
        return token.isValid() && token.getExpiration().after(now);
    }

    private void updateTokenStatus(Jwt token) {
        token.setValid(false);
        tokenRepository.save(token);
    }
}
