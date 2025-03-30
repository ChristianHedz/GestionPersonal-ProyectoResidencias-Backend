package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.*;
import com.chris.gestionpersonal.models.entity.Employee;

import java.io.IOException;
import java.util.List;


public interface EmployeeService {
    Employee findByEmail(String name);
    Employee findById(Long id);
    Employee register(RegisterDTO registerDTO);
    List<EmployeeDTO> listAllEmployees();
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    Employee loginGoogle(TokenGoogle tokenDto)throws IOException;
    void sendEmailWithQR(String email,String fullName);
}
