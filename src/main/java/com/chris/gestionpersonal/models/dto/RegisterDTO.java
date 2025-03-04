package com.chris.gestionpersonal.models.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String fullName;
    private String email;
    private String password;
}
