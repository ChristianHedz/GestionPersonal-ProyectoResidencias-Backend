package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.Repositories.RoleRepository;
import com.chris.gestionpersonal.Repositories.StatusRepository;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Role;
import com.chris.gestionpersonal.models.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements  EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    public Employee register(RegisterDTO registerDTO) {
        Employee employee = employeeMapper.registerDTOToEmployee(registerDTO);
        employee.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        Role role = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new ResourceNotFoundException("role","name","ROLE_EMPLOYEE"));
        Status status = statusRepository.findByName("ACTIVO")
                .orElseThrow(() -> new ResourceNotFoundException("status","name","ACTIVO"));
        employee.setRole(role);
        employee.setStatus(status);
        return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> findByEmail(String name) {
        return employeeRepository.findByEmail(name);
    }

}
