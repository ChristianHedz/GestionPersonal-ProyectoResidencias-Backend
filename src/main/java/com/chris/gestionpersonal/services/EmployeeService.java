package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Employee;
import java.util.List;


public interface EmployeeService {
    Employee findByEmail(String name);
    Employee findById(Long id);
    Employee register(RegisterDTO registerDTO);
    List<EmployeeDTO> listAllEmployees();
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
}
