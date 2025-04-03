package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import com.chris.gestionpersonal.services.AssistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AssistController {

    private final AssistService assistService;

    @GetMapping("/assist-details")
    public ResponseEntity<Page<AssistDetailsDTO>> getAllAssistDetailsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Solicitando asistencias paginadas: página {}, tamaño {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date", "entryTime"));
        Page<AssistDetailsDTO> assistDetailsPage = assistService.getAllAssistDetailsPaginated(pageRequest);

        return new ResponseEntity<>(assistDetailsPage, HttpStatus.OK);
    }
}