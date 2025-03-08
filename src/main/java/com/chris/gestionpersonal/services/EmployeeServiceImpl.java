package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements  EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final AuthService authService;

    public Employee register(RegisterDTO registerDTO) {
        Employee employee = employeeMapper.registerDTOToEmployee(registerDTO);
        return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> findByEmail(String name) {
        return Optional.empty();
    }
}
