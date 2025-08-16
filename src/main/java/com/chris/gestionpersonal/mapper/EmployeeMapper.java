package com.chris.gestionpersonal.mapper;

import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.AuthResponse;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.entity.Assist;
import com.chris.gestionpersonal.models.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "assists", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "qrCode", ignore = true)
    Employee registerDTOToEmployee(RegisterDTO registerDTO);

    @Mapping(target = "status", source = "status.name")
    @Mapping(target = "photo", source = "photo")
    @Mapping(target = "password", ignore = true)
    EmployeeDTO employeeToEmployeeDTO(Employee employee);

    @Mapping(target = "role", source = "role.name")
    AuthResponse employeeToAuthResponse(Employee employee);

    List<EmployeeDTO> employeeListToEmployeeDTOList(Iterable<Employee> all);

    Assist assistDTOToAssist(AssistDTO assistDTO);

    AssistDTO assistToAssistDTO(Assist assist);
}