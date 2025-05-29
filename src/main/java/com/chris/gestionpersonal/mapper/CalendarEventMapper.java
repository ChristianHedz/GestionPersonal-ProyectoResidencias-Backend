package com.chris.gestionpersonal.mapper;

import com.chris.gestionpersonal.models.dto.CalendarEventDTO;
import com.chris.gestionpersonal.models.entity.CalendarEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CalendarEventMapper {

    @Mapping(target = "employeeIds", ignore = true)
    CalendarEventDTO toDTO(CalendarEvent calendarEvent);

    @Mapping(target = "employees", ignore = true)
    CalendarEvent toEntity(CalendarEventDTO calendarEventDTO);

    void updateEntityFromDto(CalendarEventDTO dto, @MappingTarget CalendarEvent entity);

}
