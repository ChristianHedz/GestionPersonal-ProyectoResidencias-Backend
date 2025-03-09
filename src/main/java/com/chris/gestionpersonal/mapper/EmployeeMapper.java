package com.chris.gestionpersonal.mapper;

import com.chris.gestionpersonal.models.dto.AuthResponse;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "assists", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "qrCode", ignore = true)
    Employee registerDTOToEmployee(RegisterDTO registerDTO);

    EmployeeDTO employeeToEmployeeDTO(Employee employee);

    AuthResponse employeeToAuthResponse(Employee employee);
}