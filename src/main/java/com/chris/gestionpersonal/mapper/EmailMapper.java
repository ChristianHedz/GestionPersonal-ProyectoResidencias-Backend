package com.chris.gestionpersonal.mapper;

import com.chris.gestionpersonal.models.dto.EmailDTO;
import com.chris.gestionpersonal.models.dto.EmailFileDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper {
    EmailDTO emailFileDtoToEmailDto(EmailFileDTO emailFileDTO);
}

