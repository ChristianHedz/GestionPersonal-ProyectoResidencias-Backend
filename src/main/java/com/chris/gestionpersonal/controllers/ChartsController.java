package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.AttendanceSummaryDTO;
import com.chris.gestionpersonal.models.dto.EmployeeAttendanceStats;
import com.chris.gestionpersonal.models.dto.EmployeeWorkedHoursDTO;
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

    @GetMapping()
    public ResponseEntity<List<EmployeeAttendanceStats>> getAttedanceStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<EmployeeAttendanceStats> stats = assistService.getEmployeeIncidents(startDate, endDate);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/attendance-stats")
    public ResponseEntity<AttendanceSummaryDTO> getAttendanceSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AttendanceSummaryDTO attendanceSummary = assistService.totalAbsencesAndTardinessByDateRange(startDate, endDate);
        return new ResponseEntity<>(attendanceSummary, HttpStatus.OK);
    }

    @GetMapping("/worked-hours")
    public ResponseEntity<List<EmployeeWorkedHoursDTO>> getEmployeesWorkedHours(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<EmployeeWorkedHoursDTO> workedHours = assistService.getAllEmployeesWorkedHours(startDate, endDate);
        return new ResponseEntity<>(workedHours, HttpStatus.OK);
    }


}
