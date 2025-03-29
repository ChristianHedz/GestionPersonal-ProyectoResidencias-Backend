package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.AssistDTO;

public interface AssistService {
    AssistDTO assist(AssistDTO assistDTO);
    void processDailyAssists();
}
