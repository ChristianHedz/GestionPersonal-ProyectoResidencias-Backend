package com.chris.gestionpersonal.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AvailableVacationsDays {
    private String fullName;
    private Integer availableVacationDays;
}