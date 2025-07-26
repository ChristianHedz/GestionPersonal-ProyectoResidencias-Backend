package com.chris.gestionpersonal.mapper;

import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import com.chris.gestionpersonal.models.entity.Assist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssistMapper {
    Assist assistDTOToAssist(AssistDTO assistDTO);
    AssistDTO assistToAssistDTO(Assist assist);
    
    @Mapping(source = "employee.fullName", target = "fullName")
    AssistDetailsDTO assistToAssistDetailsDTO(Assist assist);
}
