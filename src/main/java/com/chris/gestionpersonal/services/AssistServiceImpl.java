package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.AssistRepository;
import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.mapper.AssistMapper;
import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.entity.Assist;
import com.chris.gestionpersonal.models.entity.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public AssistDTO assist(AssistDTO assistDTO) {
        Employee employee = employeeService.findByEmail(assistDTO.getEmailEmployee());
        Assist assist = assistMapper.assistDTOToAssist(assistDTO);
        assist.setEmployee(employee);
        assistRepository.save(assist);
        return assistMapper.assistToAssistDTO(assist);
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
                .filter(a -> a.getEntryTime() != null && a.getExitTime() == null)
                .peek(a -> {
                    a.setExitTime(exitTime);
                    a.setWorkedHours((int) ChronoUnit.HOURS.between(a.getEntryTime(), exitTime));
                    a.setIncidents("ASISTENCIA");
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