package com.chris.gestionpersonal.controllers;


import com.chris.gestionpersonal.models.dto.AvailableVacationsDays;
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
        log.info("Fetching all employees");
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

    @GetMapping("/available-vacations")
    public ResponseEntity<List<AvailableVacationsDays>> getEmployeeAvailableVacationDay() {
        List<AvailableVacationsDays> availableVacationDays = employeeService.getEmployeeAvailableVacationDay();
        return new ResponseEntity<>(availableVacationDays, HttpStatus.OK);
    }

}
