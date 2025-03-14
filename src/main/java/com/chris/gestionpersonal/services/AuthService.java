package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.Repositories.TokenRepository;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.AuthResponse;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.models.dto.LoginDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public EmployeeDTO findLoggerUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken){
            throw new AuthenticationCredentialsNotFoundException("El usuario no esta autenticado ");
        }
        log.info("Usuario autenticado 1: {}",auth.getName());
        String employee =  auth.getName();
        log.info("Usuario autenticado 2: {}",employee);
        Employee authEmployee  = employeeRepository.findByEmail(employee).orElseThrow(() -> new ResourceNotFoundException("employee",EMAIL,employee));
        return employeeMapper.employeeToEmployeeDTO(authEmployee);
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

    public List<EmployeeDTO> listAllEmployees() {
        List<EmployeeDTO> employeeList = employeeMapper.employeeListToEmployeeDTOList(employeeRepository.findAll());
        if (employeeList.isEmpty()){
            throw new ResourceNotFoundException("Employees");
        }
        return employeeList;
    }
}
