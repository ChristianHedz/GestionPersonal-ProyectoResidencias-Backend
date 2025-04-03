package com.chris.gestionpersonal.models.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AssistDetailsDTO {
    private Long id;
    private LocalDate date;
    private LocalTime entryTime;
    private LocalTime exitTime;
    private String incidents;
    private Integer workedHours;
    private String fullName;
}