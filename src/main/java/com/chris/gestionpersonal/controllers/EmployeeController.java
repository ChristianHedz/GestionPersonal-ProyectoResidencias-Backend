package com.chris.gestionpersonal.controllers;


import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.services.AssistService;
import com.chris.gestionpersonal.services.AuthService;
import com.chris.gestionpersonal.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    private final AuthService authService;
    private final EmployeeService employeeService;
    private final AssistService assistService;

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(){
        List<EmployeeDTO> response = employeeService.listAllEmployees();
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

    @PostMapping("/assist")
    public ResponseEntity<AssistDTO> assist(@RequestBody AssistDTO assistDTO){
        log.info("assist", assistDTO);
        AssistDTO response = assistService.assist(assistDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/process-assists")
    public ResponseEntity<String> testProcessAssists() {
        assistService.processDailyAssists();
        return ResponseEntity.ok("Proceso ejecutado correctamente");
    }

}
