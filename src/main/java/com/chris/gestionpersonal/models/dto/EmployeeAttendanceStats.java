package com.chris.gestionpersonal.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class EmployeeAttendanceStats {
    private String fullName;
    private Long lateCount;
    private Long absenceCount;
}
