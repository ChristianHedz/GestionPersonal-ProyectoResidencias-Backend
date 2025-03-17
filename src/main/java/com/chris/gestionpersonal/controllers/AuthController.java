package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.AuthResponse;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.models.dto.LoginDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping ("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO loginDTO, HttpServletResponse httpResponse){
        AuthResponse response = authService.login(loginDTO , httpResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterDTO registerDTO, HttpServletResponse httpResponse){
        log.info("Registering user: {}", registerDTO);
        AuthResponse response = authService.register(registerDTO,httpResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> findLoggerUser(){
        AuthResponse response = authService.findLoggerUser();
        log.info("response profile: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(){
        List<EmployeeDTO> response = authService.listAllEmployees();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        authService.logout(httpRequest,httpResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
