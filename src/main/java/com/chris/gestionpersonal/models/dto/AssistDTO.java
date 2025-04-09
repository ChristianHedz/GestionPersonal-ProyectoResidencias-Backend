package com.chris.gestionpersonal.models.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AssistDTO {
    private LocalDate date;
    private LocalTime entryTime;
    private String emailEmployee;
    private String incidents;
    private String reason;
}
