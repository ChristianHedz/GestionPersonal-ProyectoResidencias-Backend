package com.chris.gestionpersonal.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
}
