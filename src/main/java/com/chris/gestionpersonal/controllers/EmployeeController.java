package com.chris.gestionpersonal.controllers;


import com.chris.gestionpersonal.models.dto.AvailableVacationsDays;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.services.AssistService;
import com.chris.gestionpersonal.services.AuthService;
import com.chris.gestionpersonal.services.EmployeeService;
import com.chris.gestionpersonal.services.PhotoStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    private final AuthService authService;
    private final EmployeeService employeeService;
    private final AssistService assistService;
    private final PhotoStorageService photoStorageService;

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(){
        log.info("Fetching all employees");
        List<EmployeeDTO> response = employeeService.listAllEmployees();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        log.info("Fetching employee with id {}", id);
        EmployeeDTO response = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/employee/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        log.info("id", id);
        log.info("employee", employeeDTO);
        log.info("employee", employeeDTO.getFullName());
        EmployeeDTO response = employeeService.updateEmployee(id,employeeDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/available-vacations")
    public ResponseEntity<List<AvailableVacationsDays>> getEmployeeAvailableVacationDay() {
        List<AvailableVacationsDays> availableVacationDays = employeeService.getEmployeeAvailableVacationDay();
        return new ResponseEntity<>(availableVacationDays, HttpStatus.OK);
    }

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
