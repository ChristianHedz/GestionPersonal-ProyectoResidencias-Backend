package com.chris.gestionpersonal.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "{register.fullName.notBlank}")
    private String fullName;

    @NotBlank(message = "{register.email.notBlank}")
    @Pattern(regexp = "^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$", message = "{register.email.format}")
    private String email;

    @NotBlank(message = "{register.password.notBlank}")
    private String password;
}