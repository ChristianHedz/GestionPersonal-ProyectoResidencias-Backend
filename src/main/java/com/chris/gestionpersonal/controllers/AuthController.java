package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.AuthResponse;
import com.chris.gestionpersonal.models.dto.LoginDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO loginDTO){
        AuthResponse response = authService.login(loginDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDTO registerDTO){
        AuthResponse response = authService.register(registerDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
