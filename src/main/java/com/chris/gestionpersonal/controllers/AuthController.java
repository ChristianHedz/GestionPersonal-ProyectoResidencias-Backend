package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.*;
import com.chris.gestionpersonal.services.AuthService;
import com.chris.gestionpersonal.services.EmployeeService;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping ("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmployeeService employeeService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse httpResponse){
        AuthResponse response = authService.login(loginDTO , httpResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterDTO registerDTO, HttpServletResponse httpResponse) throws IOException, WriterException {
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        authService.logout(httpRequest,httpResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/authGoogle")
    public ResponseEntity<AuthResponse> loginGoogle(@RequestBody TokenGoogle tokenDto, HttpServletResponse httpResponse) throws IOException {
        AuthResponse employeeGoogleDTO = authService.loginGoogle(tokenDto, httpResponse);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeGoogleDTO);
    }

}
