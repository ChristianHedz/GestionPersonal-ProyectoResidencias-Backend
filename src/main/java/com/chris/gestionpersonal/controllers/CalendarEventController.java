package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.CalendarEventDTO;
import com.chris.gestionpersonal.services.CalendarEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/calendar-events")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @PostMapping
    public ResponseEntity<CalendarEventDTO> createEvent(@RequestBody CalendarEventDTO calendarEventDTO) {
        CalendarEventDTO createdEvent = calendarEventService.createEvent(calendarEventDTO);
        log.info("Calendar event created with id {}", createdEvent.getId());
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarEventDTO> updateEvent(@PathVariable Long id, @RequestBody CalendarEventDTO calendarEventDTO) {
        CalendarEventDTO updatedEvent = calendarEventService.updateEvent(id, calendarEventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        calendarEventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarEventDTO> getEventById(@PathVariable Long id) {
        CalendarEventDTO event = calendarEventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<List<CalendarEventDTO>> getAllEvents() {
        log.info("Fetching all calendar events");
        List<CalendarEventDTO> events = calendarEventService.getAllEvents();
        log.info("Fetched {} calendar events", events.size());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<CalendarEventDTO>> getEventsByEmployeeId(@PathVariable Long employeeId) {
        List<CalendarEventDTO> events = calendarEventService.getEventsByEmployeeId(employeeId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<CalendarEventDTO>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<CalendarEventDTO> events = calendarEventService.getEventsByDateRange(startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/employee/{employeeId}/date-range")
    public ResponseEntity<List<CalendarEventDTO>> getEventsByEmployeeIdAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<CalendarEventDTO> events = calendarEventService.getEventsByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        return ResponseEntity.ok(events);
    }
}