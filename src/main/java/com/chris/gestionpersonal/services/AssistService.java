package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AssistService {
    AssistDTO assist(AssistDTO assistDTO);
    Page<AssistDetailsDTO> getAllAssistDetailsPaginated(int page, int size, String sortBy, String sortDir, Long employeeId, String incidence, LocalDate startDate, LocalDate endDate);
    void processDailyAssists();
}
