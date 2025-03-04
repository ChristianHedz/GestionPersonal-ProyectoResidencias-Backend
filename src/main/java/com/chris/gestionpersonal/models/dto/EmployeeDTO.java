package com.chris.gestionpersonal.models.dto;

import com.chris.gestionpersonal.models.entity.Status;
import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String photo;
    private Status status;
}
