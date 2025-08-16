package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.config.CookieConfig;
import com.chris.gestionpersonal.repositories.EmployeeRepository;
import com.chris.gestionpersonal.repositories.TokenRepository;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.*;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final TokenRepository tokenRepository;
    private final CookieConfig cookieConfig;
    private static final String EMAIL = "email";

    public AuthResponse login(LoginDTO loginDTO, HttpServletResponse httpResponse ) {
        Authentication auth =  new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),loginDTO.getPassword()
        );
        authManager.authenticate(auth);
        Employee employee = employeeRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Employee",EMAIL, loginDTO.getEmail()));
        String jwt = jwtService.generateToken(employee,generateExtraClaims(employee));
        saveToken(jwt,employee);
        addJwtCookie(httpResponse,jwt);
        return employeeMapper.employeeToAuthResponse(employee);
    }

    public AuthResponse register(RegisterDTO registerDTO, HttpServletResponse httpResponse){
        Employee employee = employeeService.register(registerDTO);
        createToken(employee,httpResponse);
        return employeeMapper.employeeToAuthResponse(employee);
    }

    public void createToken(Employee employee, HttpServletResponse httpResponse) {
        String jwt = jwtService.generateToken(employee, generateExtraClaims(employee));
        saveToken(jwt, employee);
        addJwtCookie(httpResponse, jwt);
    }

    public AuthResponse loginGoogle(TokenGoogle tokenDto, HttpServletResponse httpResponse) throws IOException {
        Employee employee = employeeService.loginGoogle(tokenDto);
        createToken(employee,httpResponse);
        return employeeMapper.employeeToAuthResponse(employee);
    }

    private void addJwtCookie(HttpServletResponse httpResponse, String jwt) {
        Cookie jwtCookie = cookieConfig.buildJwtCookie(jwt, 60 * 60);
        httpResponse.addCookie(jwtCookie);
    }

    public AuthResponse findLoggerUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken){
            throw new AuthenticationCredentialsNotFoundException("El usuario no esta autenticado ");
        }
        String employee =  auth.getName();
        log.info("Usuario autenticado 2: {}",employee);
        Employee authEmployee  = employeeRepository.findByEmail(employee).orElseThrow(()
                -> new ResourceNotFoundException("employee",EMAIL,employee));
        return employeeMapper.employeeToAuthResponse(authEmployee);
    }

    private Map<String,Object> generateExtraClaims(Employee employee) {
        return Map.of( EMAIL,employee.getEmail(),
                  "authorities",employee.getAuthorities()
        );
    }

    private void saveToken(String jwt,Employee employee) {
        Jwt token = new Jwt();
        token.setEmployee(employee);
        token.setToken(jwt);
        token.setExpiration(jwtService.extractExpiration(jwt));
        token.setValid(true);
        tokenRepository.save(token);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {

        Optional<String> jwt = jwtService.extractJwtFromCookie(request);
        if(jwt.isEmpty()){
            return;
        }
        Optional<Jwt> token = tokenRepository.findByToken(jwt.get());

        if(token.isPresent()  && token.get().isValid()){
            token.get().setValid(false);
            tokenRepository.save(token.get());
        }

        Cookie cookie = cookieConfig.buildClearedJwtCookie();
        response.addCookie(cookie);
    }


}
