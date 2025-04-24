package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.EmployeeAttendanceStats;
import com.chris.gestionpersonal.services.AssistService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/charts")
public class ChartsController {

    private final AssistService assistService;

    @GetMapping
    public ResponseEntity<List<EmployeeAttendanceStats>> getCharts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<EmployeeAttendanceStats> stats = assistService.getEmployeeIncidents(startDate, endDate);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }


}
