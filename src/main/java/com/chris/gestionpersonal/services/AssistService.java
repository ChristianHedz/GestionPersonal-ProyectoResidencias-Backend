package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import com.chris.gestionpersonal.models.dto.EmployeeAttendanceStats;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.util.List;

public interface AssistService {
    AssistDTO assist(AssistDTO assistDTO);
    Page<AssistDetailsDTO> getAllAssistDetailsPaginated(int page, int size, String sortBy, String sortDir, Long employeeId, String incidence, LocalDate startDate, LocalDate endDate);
    byte[] exportAssistDetailsToExcel(String sortBy, String sortDir, Long employeeId, String incidence, LocalDate startDate, LocalDate endDate);
    List<EmployeeAttendanceStats> getEmployeeIncidents(LocalDate startDate, LocalDate endDate);
    void processDailyAssists();
}
