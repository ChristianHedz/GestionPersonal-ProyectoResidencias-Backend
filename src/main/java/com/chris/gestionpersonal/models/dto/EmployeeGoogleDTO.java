package com.chris.gestionpersonal.models.dto;

import lombok.Data;

@Data
public class EmployeeGoogleDTO {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private boolean isError;
}