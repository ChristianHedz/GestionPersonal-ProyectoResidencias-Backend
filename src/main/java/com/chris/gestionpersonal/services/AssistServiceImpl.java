package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.AssistRepository;
import com.chris.gestionpersonal.Repositories.AssistSpecifications;
import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.exceptions.InvalidDateRangeException;
import com.chris.gestionpersonal.mapper.AssistMapper;
import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import com.chris.gestionpersonal.models.dto.EmployeeAttendanceStats;
import com.chris.gestionpersonal.models.entity.Assist;
import com.chris.gestionpersonal.models.entity.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistServiceImpl implements AssistService {

    private final AssistRepository assistRepository;
    private final EmployeeRepository employeeRepository;
    private final AssistMapper assistMapper;
    private final EmployeeService employeeService;
    private final AssistExcelServiceImpl assistExcelService;

    @Override
    public AssistDTO assist(AssistDTO assistDTO) {
        Employee employee = employeeService.findByEmail(assistDTO.getEmailEmployee());
        Assist assist = assistMapper.assistDTOToAssist(assistDTO);
        assist.setEmployee(employee);
        assist.setWorkedHours(0);
        assistRepository.save(assist);
        return assistMapper.assistToAssistDTO(assist);
    }

    @Override
    public Page<AssistDetailsDTO> getAllAssistDetailsPaginated(int page, int size, String sortBy, String sortDir, Long employeeId, String incidence, LocalDate startDate, LocalDate endDate) {
        Sort sortByAndOrder = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sortByAndOrder);

        Specification<Assist> spec = createAssistSpecification(employeeId, incidence, startDate, endDate);

        Page<Assist> assistPage = assistRepository.findAll(spec, pageable);

        return assistPage.map(assistMapper::assistToAssistDetailsDTO);
    }

    @Override
    public byte[] exportAssistDetailsToExcel(String sortBy, String sortDir, Long employeeId, String incidence, LocalDate startDate, LocalDate endDate) {
        log.info("Exportando detalles de asistencia a Excel", sortBy, sortDir, employeeId, incidence, startDate, endDate);
        Sort sortByAndOrder = Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Specification<Assist> spec = createAssistSpecification(employeeId, incidence, startDate, endDate);

        List<Assist> assists = assistRepository.findAll(spec, sortByAndOrder);
        List<AssistDetailsDTO> assistDetailsDTOs = assists.stream()
                .map(assistMapper::assistToAssistDetailsDTO)
                .toList();
        log.info("Exportando detalles de asistencia a Excel finalizado", assistDetailsDTOs.size());
        return assistExcelService.createExcel(assistDetailsDTOs);
    }

    private Specification<Assist> createAssistSpecification(Long employeeId, String incidence, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("La fecha de inicio debe ser anterior o igual a la fecha de fin");
        }

        return Specification.where(AssistSpecifications.withEmployeeId(employeeId))
                .and(AssistSpecifications.withIncidence(incidence))
                .and(AssistSpecifications.withDateBetween(startDate, endDate));
    }

    @Override
    public List<EmployeeAttendanceStats> getEmployeeIncidents(LocalDate startDate, LocalDate endDate) {
        return assistRepository.findEmployeeAttendanceStatsByDateRange(startDate, endDate);
    }

    @Scheduled(cron = "0 5 18 * * ?") // Se ejecuta a las 18:05 todos los días
    public void processDailyAssists() {
        log.info("Inicia el proceso de asistencia diaria");
        LocalDate today = LocalDate.now();
        LocalTime exitTime = LocalTime.of(18, 0);

        List<Employee> activeEmployees = employeeRepository.findByStatus_Name("ACTIVO");
        List<Assist> todayAssists = assistRepository.findByDate(today);
        Set<Long> employeeIdsWithAttendance = getEmployeeIdsWithAssist(todayAssists);

        completeIncompleteAssists(todayAssists, exitTime);
        registerMissingAssists(activeEmployees, employeeIdsWithAttendance, today);

        log.info("Proceso de asistencia diaria finalizado");
    }

    private Set<Long> getEmployeeIdsWithAssist(List<Assist> assists) {
        return assists.stream()
                .map(a -> a.getEmployee().getId())
                .collect(Collectors.toSet());
    }

    private void completeIncompleteAssists(List<Assist> assists, LocalTime exitTime) {
        assistRepository.saveAll(assists.stream()
                .filter(assist -> assist.getEntryTime() != null && assist.getExitTime() == null)
                .map(assist -> {
                    assist.setExitTime(exitTime);
                    assist.setWorkedHours((int) ChronoUnit.HOURS.between(assist.getEntryTime(), exitTime));
                    assist.setIncidents("ASISTENCIA");
                    return assist;
                })
                .toList());
    }

    private void registerMissingAssists(List<Employee> employees, Set<Long> idsWithAssist, LocalDate date) {
        assistRepository.saveAll(employees.stream()
                .filter(e -> !idsWithAssist.contains(e.getId()))
                .map(e -> createAbsenceRecord(e, date))
                .toList());
    }

    private Assist createAbsenceRecord(Employee employee, LocalDate date) {
        Assist absence = new Assist();
        absence.setEmployee(employee);
        absence.setDate(date);
        absence.setIncidents("FALTA");
        absence.setWorkedHours(0);
        return absence;
    }


}