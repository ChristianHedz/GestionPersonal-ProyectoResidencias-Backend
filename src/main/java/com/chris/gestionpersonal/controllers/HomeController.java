package com.chris.gestionpersonal.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Home", description = "Endpoint de información general de la API")
@RestController
public class HomeController {

    @Operation(summary = "Información de la API", description = "Obtiene información general sobre la API de Gestión Personal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "message", "API de Gestión Personal",
            "status", "OK",
            "version", "1.0.0"
        );
    }
}
