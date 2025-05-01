package com.chris.gestionpersonal.repositories;

import com.chris.gestionpersonal.models.dto.AttendanceSummaryDTO;
import com.chris.gestionpersonal.models.dto.EmployeeAttendanceStats;
import com.chris.gestionpersonal.models.dto.EmployeeWorkedHoursDTO;
import com.chris.gestionpersonal.models.entity.Assist;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AssistRepository extends CrudRepository<Assist, Long>, JpaSpecificationExecutor<Assist> {
    List<Assist> findByDate(LocalDate date);

    @Query("SELECT NEW com.chris.gestionpersonal.models.dto.EmployeeAttendanceStats(" +
            "e.fullName, " +
            "SUM(CASE WHEN a.incidents = 'RETARDO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.incidents = 'FALTA' THEN 1 ELSE 0 END)) " +
            "FROM Assist a JOIN a.employee e " +
            "WHERE a.date BETWEEN :startDate AND :endDate " +
            "GROUP BY e.id, e.fullName")
    List<EmployeeAttendanceStats> findEmployeeAttendanceStatsByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT NEW com.chris.gestionpersonal.models.dto.AttendanceSummaryDTO(" +
            "SUM(CASE WHEN a.incidents = 'FALTA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.incidents = 'RETARDO' THEN 1 ELSE 0 END)) " +
            "FROM Assist a " +
            "WHERE a.date BETWEEN :startDate AND :endDate")
    AttendanceSummaryDTO findTotalAbsencesAndTardinessByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT NEW com.chris.gestionpersonal.models.dto.EmployeeWorkedHoursDTO(" +
            "e.fullName, " +
            "SUM(a.workedHours)) " +
            "FROM Assist a JOIN a.employee e " +
            "WHERE a.date BETWEEN :startDate AND :endDate " +
            "GROUP BY e.id, e.fullName")
    List<EmployeeWorkedHoursDTO> findEmployeeWorkedHoursByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
