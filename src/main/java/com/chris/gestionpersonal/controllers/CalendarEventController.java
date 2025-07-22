package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.CalendarEventDTO;
import com.chris.gestionpersonal.services.CalendarEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Calendar Events", description = "Endpoints para gestión de eventos de calendario")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/calendar-events")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @Operation(summary = "Crear evento de calendario", description = "Crea un nuevo evento en el calendario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalendarEventDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Datos del evento inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping
    public ResponseEntity<CalendarEventDTO> createEvent(@RequestBody CalendarEventDTO calendarEventDTO) {
        CalendarEventDTO createdEvent = calendarEventService.createEvent(calendarEventDTO);
        log.info("Calendar event created with id {}", createdEvent.getId());
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar evento", description = "Actualiza un evento de calendario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalendarEventDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos del evento inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PutMapping("/{id}")
    public ResponseEntity<CalendarEventDTO> updateEvent(@PathVariable Long id, @RequestBody CalendarEventDTO calendarEventDTO) {
        CalendarEventDTO updatedEvent = calendarEventService.updateEvent(id, calendarEventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(summary = "Eliminar evento", description = "Elimina un evento de calendario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        calendarEventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener evento por ID", description = "Obtiene un evento específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalendarEventDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/{id}")
    public ResponseEntity<CalendarEventDTO> getEventById(@PathVariable Long id) {
        CalendarEventDTO event = calendarEventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Obtener todos los eventos", description = "Obtiene todos los eventos de calendario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos obtenidos exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalendarEventDTO.class))}),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping
    public ResponseEntity<List<CalendarEventDTO>> getAllEvents() {
        log.info("Fetching all calendar events");
        List<CalendarEventDTO> events = calendarEventService.getAllEvents();
        log.info("Fetched {} calendar events", events.size());
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Obtener eventos por empleado", description = "Obtiene todos los eventos de un empleado específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos del empleado obtenidos exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalendarEventDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<CalendarEventDTO>> getEventsByEmployeeId(@PathVariable Long employeeId) {
        List<CalendarEventDTO> events = calendarEventService.getEventsByEmployeeId(employeeId);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Obtener eventos por rango de fechas", description = "Obtiene eventos dentro de un rango de fechas específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos obtenidos exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalendarEventDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/date-range")
    public ResponseEntity<List<CalendarEventDTO>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<CalendarEventDTO> events = calendarEventService.getEventsByDateRange(startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Obtener eventos por empleado y rango de fechas", description = "Obtiene eventos de un empleado específico dentro de un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos obtenidos exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CalendarEventDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @GetMapping("/employee/{employeeId}/date-range")
    public ResponseEntity<List<CalendarEventDTO>> getEventsByEmployeeIdAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<CalendarEventDTO> events = calendarEventService.getEventsByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        return ResponseEntity.ok(events);
    }
}