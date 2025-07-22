package com.chris.gestionpersonal.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API GESTIÓN PERSONAL",
                description = "Sistema completo de gestión de personal que incluye autenticación, registro de asistencias, gestión de empleados, eventos de calendario, reportes y servicios de comunicación",
                termsOfService = "www.pasteleriaprimavera.com/terminos_y_condiciones",
                version = "1.0.0",
                contact = @Contact(
                        name = "Cristian",
                        url = "https://pasteleriaprimavera.social",
                        email = "cristian.28hedz@gmail.com"
                ),
                license = @License(
                        name = "Standard Software Use License for Gestión Personal",
                        url = "www.pasteleriaprimavera.com/licence"
                )
        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:8081"
                ),
                @Server(
                        description = "PROD SERVER",
                        url = "https://pasteleriaprimavera.social"
                )
        },
        security = @SecurityRequirement(
                name = "Security Token"
        )
)
@SecurityScheme(
        name = "Security Token",
        description = "JWT Access Token para autenticación en la API de Gestión Personal",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    // Configuración completa de OpenAPI/Swagger para documentación de la API
    // Incluye esquema de seguridad JWT para endpoints protegidos
}
