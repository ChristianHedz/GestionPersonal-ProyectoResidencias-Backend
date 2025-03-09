package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.Repositories.TokenRepository;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.AuthResponse;
import com.chris.gestionpersonal.models.dto.LoginDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final TokenRepository tokenRepository;
    private static final String EMAIL = "email";

    public AuthResponse login(LoginDTO loginDTO) {
        Authentication auth =  new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),loginDTO.getPassword()
        );
        authManager.authenticate(auth);
        Employee employee = employeeRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Employee",EMAIL, loginDTO.getEmail()));
        String jwt = jwtService.generateToken(employee,generateExtraClaims(employee));
        saveToken(jwt,employee);
        AuthResponse authResponse = employeeMapper.employeeToAuthResponse(employee);
        authResponse.setToken(jwt);
        return authResponse;
    }

    public AuthResponse register(RegisterDTO registerDTO) {
        Employee employee = employeeService.register(registerDTO);
        String jwt = jwtService.generateToken(employee,generateExtraClaims(employee));
        saveToken(jwt,employee);
        AuthResponse authResponse = employeeMapper.employeeToAuthResponse(employee);
        authResponse.setToken(jwt);
        return authResponse;

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

}
