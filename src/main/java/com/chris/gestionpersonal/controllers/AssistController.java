package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.config.AppConstants.Pagination;
import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import com.chris.gestionpersonal.services.AssistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Attendance Management", description = "Endpoints para gestión de asistencias")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AssistController {

    private final AssistService assistService;

    @Operation(summary = "Obtener detalles de asistencias paginados", description = "Obtiene una lista paginada de detalles de asistencias con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Detalles de asistencias obtenidos exitosamente",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AssistDetailsDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/assist-details")
    public ResponseEntity<Page<AssistDetailsDTO>> getAllAssistDetailsPaginated(
            @RequestParam(defaultValue = Pagination.PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Pagination.PAGE_SIZE) int size,
            @RequestParam(defaultValue = Pagination.SORT_BY) String sortBy,
            @RequestParam(defaultValue = Pagination.SORT_DIRECTION) String sortDirection,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String incidents,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Fetching assist details with pagination: page={}, size={}, sortBy={}, sortDirection={}, employeeId={}, incidence={}, startDate={}, endDate={}",
                page, size, sortBy, sortDirection, employeeId, incidents, startDate, endDate);
        Page<AssistDetailsDTO> assistDetailsPage = assistService.
                getAllAssistDetailsPaginated(page, size, sortBy, sortDirection, employeeId, incidents, startDate, endDate);

        return new ResponseEntity<>(assistDetailsPage, HttpStatus.OK);
    }

    @Operation(summary = "Exportar asistencias a Excel", description = "Exporta los detalles de asistencias a un archivo Excel con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo Excel generado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de filtro inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/assist-details/excel")
    public ResponseEntity<byte[]> exportAssistDetailsToExcel(
            @RequestParam(defaultValue = Pagination.SORT_BY) String sortBy,
            @RequestParam(defaultValue = Pagination.SORT_DIRECTION) String sortDirection,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String incidents,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Exporting assist details to Excel: sortBy={}, sortDirection={}, employeeId={}, incidence={}, startDate={}, endDate={}",
                sortBy, sortDirection, employeeId, incidents, startDate, endDate);
        byte[] excelData = assistService.exportAssistDetailsToExcel(sortBy, sortDirection, employeeId, incidents, startDate, endDate);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=assist_details.xlsx")
                .body(excelData);
    }

    @Operation(summary = "Registrar asistencia", description = "Registra una nueva asistencia para un empleado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Asistencia registrada exitosamente",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AssistDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Datos de asistencia inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping("/assist")
    public ResponseEntity<AssistDTO> assist(@RequestBody AssistDTO assistDTO){
        log.info("assist", assistDTO);
        AssistDTO response = assistService.assist(assistDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Procesar asistencias diarias", description = "Ejecuta el proceso de cálculo y análisis de asistencias diarias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proceso ejecutado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/process-assists")
    public ResponseEntity<String> testProcessAssists() {
        assistService.processDailyAssists();
        return ResponseEntity.ok("Proceso ejecutado correctamente");
    }

}