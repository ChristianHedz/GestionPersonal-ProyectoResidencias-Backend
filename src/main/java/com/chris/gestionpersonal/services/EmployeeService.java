package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Employee;

import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> findByEmail(String name);
    Employee register(RegisterDTO registerDTO);
}
