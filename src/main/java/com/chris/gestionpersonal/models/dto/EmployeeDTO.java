package com.chris.gestionpersonal.models.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class EmployeeDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String photo;
    private String status;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
