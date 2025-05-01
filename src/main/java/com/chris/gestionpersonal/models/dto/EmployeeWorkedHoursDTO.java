package com.chris.gestionpersonal.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EmployeeWorkedHoursDTO {
    private String fullName;
    private Long workedHours;
}
