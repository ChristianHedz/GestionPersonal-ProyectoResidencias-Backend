package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.CalendarEventDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarEventService {
    
    CalendarEventDTO createEvent(CalendarEventDTO calendarEventDTO);
    
    CalendarEventDTO updateEvent(Long id, CalendarEventDTO calendarEventDTO);
    
    void deleteEvent(Long id);
    
    CalendarEventDTO getEventById(Long id);
    
    List<CalendarEventDTO> getAllEvents();
    
    List<CalendarEventDTO> getEventsByEmployeeId(Long employeeId);
    
    List<CalendarEventDTO> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<CalendarEventDTO> getEventsByEmployeeIdAndDateRange(Long employeeId, LocalDateTime startDate, LocalDateTime endDate);
}