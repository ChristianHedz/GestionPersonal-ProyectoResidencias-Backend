package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssistService {
    AssistDTO assist(AssistDTO assistDTO);
    Page<AssistDetailsDTO> getAllAssistDetailsPaginated(Pageable pageable);
    void processDailyAssists();
}
