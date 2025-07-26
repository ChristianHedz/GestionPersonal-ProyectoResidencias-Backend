package com.chris.gestionpersonal.services.impl;

import com.chris.gestionpersonal.exceptions.EventServiceException;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.CalendarEventMapper;
import com.chris.gestionpersonal.models.dto.CalendarEventDTO;
import com.chris.gestionpersonal.models.entity.CalendarEvent;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.repositories.CalendarEventRepository;
import com.chris.gestionpersonal.repositories.EmployeeRepository;
import com.chris.gestionpersonal.services.CalendarEventService;
import com.chris.gestionpersonal.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalendarEventServiceImpl implements CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final EmployeeRepository employeeRepository;
    private final CalendarEventMapper calendarEventMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public CalendarEventDTO createEvent(CalendarEventDTO calendarEventDTO) {
        log.info("Creating calendar event: {}", calendarEventDTO);
        CalendarEvent calendarEvent = calendarEventMapper.toEntity(calendarEventDTO);
        CalendarEvent savedEvent = employeeIdsToEmployees(calendarEventDTO, calendarEvent);
        log.info("Created calendar event with id: {}", savedEvent.getId());
        if (!savedEvent.getEmployees().isEmpty()) {
            Map<String, String> emailsAndNames = savedEvent.getEmployees().stream()
                    .collect(Collectors.toMap(Employee::getEmail, Employee::getFullName));
            emailService.sendBatchEventEmails(emailsAndNames, savedEvent);
        }

        return calendarEventMapper.toDTO(savedEvent);
    }

    private CalendarEvent employeeIdsToEmployees(CalendarEventDTO calendarEventDTO, CalendarEvent calendarEvent) {
        if (calendarEventDTO.getEmployeeIds() != null && !calendarEventDTO.getEmployeeIds().isEmpty()) {
            Set<Employee> employees = new HashSet<>();
            for (Long employeeId : calendarEventDTO.getEmployeeIds()) {
                employeeRepository.findById(employeeId).ifPresent(employees::add);
            }
            calendarEvent.setEmployees(employees);
        }

        return calendarEventRepository.save(calendarEvent);
    }


    @Override
    @Transactional
    public CalendarEventDTO updateEvent(Long id, CalendarEventDTO calendarEventDTO) {
        CalendarEvent existingEvent = calendarEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event not found with id: " + id));

        existingEvent.setTitle(calendarEventDTO.getTitle());
        existingEvent.setDescription(calendarEventDTO.getDescription());
        existingEvent.setStartDate(calendarEventDTO.getStartDate());
        existingEvent.setEndDate(calendarEventDTO.getEndDate());
        existingEvent.setEventType(calendarEventDTO.getEventType());
        CalendarEvent updatedEvent = employeeIdsToEmployees(calendarEventDTO, existingEvent);
        return calendarEventMapper.toDTO(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        if (!calendarEventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Calendar event not found with id: " + id);
        }
        calendarEventRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarEventDTO getEventById(Long id) {
        CalendarEvent calendarEvent = calendarEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event not found with id: " + id));
        return calendarEventMapper.toDTO(calendarEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getAllEvents() {
        try {
            List<CalendarEvent> events = calendarEventRepository.findAll();
            return events.stream()
                    .map(calendarEventMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            log.error("Error retrieving all calendar events", e);
            throw new EventServiceException("Failed to retrieve all calendar events due to an unexpected error.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getEventsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        List<CalendarEvent> events = calendarEventRepository.findByEmployeesContaining(employee);
        return events.stream()
                .map(calendarEventMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<CalendarEvent> events = calendarEventRepository.findByStartDateBetween(startDate, endDate);
        return events.stream()
                .map(calendarEventMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getEventsByEmployeeIdAndDateRange(Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        List<CalendarEvent> events = calendarEventRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        return events.stream()
                .map(calendarEventMapper::toDTO)
                .toList();
    }
}
