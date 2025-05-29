package com.chris.gestionpersonal.repositories;

import com.chris.gestionpersonal.models.entity.CalendarEvent;
import com.chris.gestionpersonal.models.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    
    List<CalendarEvent> findByEmployeesContaining(Employee employee);
    
    List<CalendarEvent> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT ce FROM CalendarEvent ce JOIN ce.employees e WHERE e.id = :employeeId AND ce.startDate BETWEEN :startDate AND :endDate")
    List<CalendarEvent> findByEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}