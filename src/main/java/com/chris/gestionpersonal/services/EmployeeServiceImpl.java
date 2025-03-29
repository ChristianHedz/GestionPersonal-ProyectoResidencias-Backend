package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.AssistRepository;
import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.Repositories.RoleRepository;
import com.chris.gestionpersonal.Repositories.StatusRepository;
import com.chris.gestionpersonal.exceptions.EmailAlreadyRegisteredException;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Assist;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Role;
import com.chris.gestionpersonal.models.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements  EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final AssistRepository assistRepository;

    public Employee register(RegisterDTO registerDTO) {
        validateEmailAvailability(registerDTO.getEmail());
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
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("employee","email",email));
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("employee","id",id));
    }

    private void validateEmailAvailability(String email){
        employeeRepository.findByEmail(email)
                .ifPresent(employee -> {
                    throw new EmailAlreadyRegisteredException("El email ya se encuentra registrado");
                });
    }

    public List<EmployeeDTO> listAllEmployees() {
        List<EmployeeDTO> employeeList = employeeMapper.employeeListToEmployeeDTOList(employeeRepository.findAll());
        if (employeeList.isEmpty()){
            throw new ResourceNotFoundException("Employees");
        }
        return employeeList;
    }

    public EmployeeDTO updateEmployee(Long idEmpleado,EmployeeDTO employeeDTO) {
        Employee employee = this.findById(idEmpleado);
        employee.setFullName(employeeDTO.getFullName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhone(employeeDTO.getPhone());
        Status status = statusRepository.findByName(employeeDTO.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("status","name",employeeDTO.getStatus()));
        employee.setStatus(status);
        employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeDTO(employee);
    }


}
