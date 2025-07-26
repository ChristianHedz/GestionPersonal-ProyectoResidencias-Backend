package com.chris.gestionpersonal.controllers;


import com.chris.gestionpersonal.models.dto.AvailableVacationsDays;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.services.EmployeeService;
import com.chris.gestionpersonal.services.PhotoStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "Employee Management", description = "Endpoints para gestión de empleados")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PhotoStorageService photoStorageService;

    @Operation(summary = "Obtener todos los empleados", description = "Obtiene la lista completa de empleados")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Lista de empleados obtenida exitosamente",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDTO.class))
                    }),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(){
        log.info("Fetching all employees");
        List<EmployeeDTO> response = employeeService.listAllEmployees();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Obtener empleado por ID", description = "Obtiene los detalles de un empleado específico")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Empleado encontrado exitosamente",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDTO.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/employee/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        log.info("Fetching employee with id {}", id);
        EmployeeDTO response = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar empleado", description = "Actualiza los datos de un empleado existente")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Empleado actualizado exitosamente",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EmployeeDTO.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PutMapping("/employee/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        log.info("id", id);
        log.info("employee", employeeDTO);
        log.info("employee", employeeDTO.getFullName());
        EmployeeDTO response = employeeService.updateEmployee(id,employeeDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Obtener días de vacaciones disponibles", description = "Obtiene los días de vacaciones disponibles para todos los empleados")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Días de vacaciones obtenidos exitosamente",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AvailableVacationsDays.class))
                    }),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/available-vacations")
    public ResponseEntity<List<AvailableVacationsDays>> getEmployeeAvailableVacationDay() {
        List<AvailableVacationsDays> availableVacationDays = employeeService.getEmployeeAvailableVacationDay();
        return new ResponseEntity<>(availableVacationDays, HttpStatus.OK);
    }

    @Operation(summary = "Subir foto de empleado", description = "Sube una foto para un empleado específico a S3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto subida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "400", description = "Archivo inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping("/employee/{id}/upload-photo")
    public ResponseEntity<Map<String, String>> uploadEmployeePhoto(
            @PathVariable Long id,
            @RequestParam("photo") MultipartFile photo) {
        
        log.info("Subiendo foto para empleado con ID: {}", id);
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        
        // Subir la foto a S3
        String photoUrl = photoStorageService.uploadEmployeePhoto(photo, employee.getEmail());
        
        // Actualizar la URL de la foto en el empleado
        employee.setPhoto(photoUrl);
        employeeService.updateEmployee(id, employee);
        
        log.info("Foto subida exitosamente. URL: {}", photoUrl);
        
        return ResponseEntity.ok(Map.of(
            "message", "Foto subida exitosamente",
            "photoUrl", photoUrl
        ));
    }

    @Operation(summary = "Eliminar foto de empleado", description = "Elimina la foto de un empleado específico de S3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @DeleteMapping("/employee/{id}/delete-photo")
    public ResponseEntity<Map<String, String>> deleteEmployeePhoto(@PathVariable Long id) {
        log.info("Eliminando foto para empleado con ID: {}", id);
        
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        
        if (employee.getPhoto() != null && !employee.getPhoto().isEmpty()) {
            // Extraer el nombre del archivo de la URL
            String fileName = extractFileNameFromUrl(employee.getPhoto());
            photoStorageService.deleteEmployeePhoto(fileName);
            
            // Limpiar la URL de la foto en la base de datos
            employee.setPhoto(null);
            employeeService.updateEmployee(id, employee);
        }
        
        return ResponseEntity.ok(Map.of("message", "Foto eliminada exitosamente"));
    }

    private String extractFileNameFromUrl(String url) {
        // Extraer el nombre del archivo de la URL de S3
        // Ejemplo: https://bucket.s3.region.amazonaws.com/employee-photos/file.jpg
        if (url.contains("employee-photos/")) {
            return url.substring(url.indexOf("employee-photos/"));
        }
        return url;
    }

}
