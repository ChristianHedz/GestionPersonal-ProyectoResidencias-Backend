package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.AttendanceSummaryDTO;
import com.chris.gestionpersonal.models.dto.EmployeeAttendanceStats;
import com.chris.gestionpersonal.models.dto.EmployeeWorkedHoursDTO;
import com.chris.gestionpersonal.services.AssistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Charts & Analytics", description = "Endpoints para gráficos y estadísticas")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/charts")
public class ChartsController {

    private final AssistService assistService;

    @Operation(summary = "Obtener estadísticas de asistencia", description = "Obtiene estadísticas de incidencias de asistencia por empleado en un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeAttendanceStats.class))}),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping()
    public ResponseEntity<List<EmployeeAttendanceStats>> getAttedanceStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<EmployeeAttendanceStats> stats = assistService.getEmployeeIncidents(startDate, endDate);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @Operation(summary = "Obtener resumen de asistencia", description = "Obtiene resumen total de ausencias y tardanzas en un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceSummaryDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/attendance-stats")
    public ResponseEntity<AttendanceSummaryDTO> getAttendanceSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AttendanceSummaryDTO attendanceSummary = assistService.totalAbsencesAndTardinessByDateRange(startDate, endDate);
        return new ResponseEntity<>(attendanceSummary, HttpStatus.OK);
    }

    @Operation(summary = "Obtener horas trabajadas", description = "Obtiene las horas trabajadas por todos los empleados en un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horas trabajadas obtenidas exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeWorkedHoursDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/worked-hours")
    public ResponseEntity<List<EmployeeWorkedHoursDTO>> getEmployeesWorkedHours(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<EmployeeWorkedHoursDTO> workedHours = assistService.getAllEmployeesWorkedHours(startDate, endDate);
        return new ResponseEntity<>(workedHours, HttpStatus.OK);
    }


}
