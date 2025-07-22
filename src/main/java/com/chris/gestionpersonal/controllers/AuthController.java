package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.*;
import com.chris.gestionpersonal.services.AuthService;
import com.chris.gestionpersonal.services.EmployeeService;
import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Authentication", description = "Endpoints para autenticación de usuarios")
@Slf4j
@RestController
@RequestMapping ("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmployeeService employeeService;

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Login exitoso",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse httpResponse){
        AuthResponse response = authService.login(loginDTO , httpResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo empleado en el sistema")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Usuario registrado exitosamente",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))
                    }),
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterDTO registerDTO, HttpServletResponse httpResponse) throws IOException, WriterException {
        log.info("Registering user: {}", registerDTO);
        AuthResponse response = authService.register(registerDTO,httpResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Obtener perfil de usuario", description = "Obtiene el perfil del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Perfil obtenido exitosamente",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))
                    }),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> findLoggerUser(){
        AuthResponse response = authService.findLoggerUser();
        log.info("response profile: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        log.info("Logging out user");
        authService.logout(httpRequest,httpResponse);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Autenticación con Google", description = "Autentica un usuario usando token de Google OAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Autenticación con Google exitosa",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Token de Google inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/authGoogle")
    public ResponseEntity<AuthResponse> loginGoogle(@RequestBody TokenGoogle tokenDto, HttpServletResponse httpResponse) throws IOException {
        AuthResponse employeeGoogleDTO = authService.loginGoogle(tokenDto, httpResponse);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeGoogleDTO);
    }

}
