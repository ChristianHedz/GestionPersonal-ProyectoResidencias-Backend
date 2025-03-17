package com.chris.gestionpersonal.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "{login.email.notBlank}")
    @Pattern(regexp = "^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$", message = "{login.email.format}")
    private String email;
    @NotBlank(message = "{login.password.notBlank}")
    private String password;
}
