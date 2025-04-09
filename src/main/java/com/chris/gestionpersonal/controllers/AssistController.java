package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.config.AppConstants.Pagination;
import com.chris.gestionpersonal.models.dto.AssistDTO;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import com.chris.gestionpersonal.services.AssistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AssistController {

    private final AssistService assistService;

    @GetMapping("/assist-details")
    public ResponseEntity<Page<AssistDetailsDTO>> getAllAssistDetailsPaginated(
            @RequestParam(defaultValue = Pagination.PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Pagination.PAGE_SIZE) int size,
            @RequestParam(defaultValue = Pagination.SORT_BY) String sortBy,
            @RequestParam(defaultValue = Pagination.SORT_DIRECTION) String sortDirection,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String incidents,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Fetching assist details with pagination: page={}, size={}, sortBy={}, sortDirection={}, employeeId={}, incidence={}, startDate={}, endDate={}",
                page, size, sortBy, sortDirection, employeeId, incidents, startDate, endDate);
        Page<AssistDetailsDTO> assistDetailsPage = assistService.
                getAllAssistDetailsPaginated(page, size, sortBy, sortDirection, employeeId, incidents, startDate, endDate);

        return new ResponseEntity<>(assistDetailsPage, HttpStatus.OK);
    }

    @PostMapping("/assist")
    public ResponseEntity<AssistDTO> assist(@RequestBody AssistDTO assistDTO){
        log.info("assist", assistDTO);
        AssistDTO response = assistService.assist(assistDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/process-assists")
    public ResponseEntity<String> testProcessAssists() {
        assistService.processDailyAssists();
        return ResponseEntity.ok("Proceso ejecutado correctamente");
    }

}