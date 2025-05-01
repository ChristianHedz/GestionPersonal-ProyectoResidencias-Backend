package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.EmployeeWorkedHoursDTO;
import java.util.List;
import com.chris.gestionpersonal.models.dto.*;
import org.springframework.data.domain.Page;
import java.time.LocalDate;

public interface AssistService {
    AssistDTO assist(AssistDTO assistDTO);
    Page<AssistDetailsDTO> getAllAssistDetailsPaginated(int page, int size, String sortBy, String sortDir, Long employeeId, String incidence, LocalDate startDate, LocalDate endDate);
    byte[] exportAssistDetailsToExcel(String sortBy, String sortDir, Long employeeId, String incidence, LocalDate startDate, LocalDate endDate);
    List<EmployeeAttendanceStats> getEmployeeIncidents(LocalDate startDate, LocalDate endDate);
    void processDailyAssists();
    AttendanceSummaryDTO totalAbsencesAndTardinessByDateRange(LocalDate startDate, LocalDate endDate);
    List<EmployeeWorkedHoursDTO> getAllEmployeesWorkedHours(LocalDate startDate, LocalDate endDate);
}
