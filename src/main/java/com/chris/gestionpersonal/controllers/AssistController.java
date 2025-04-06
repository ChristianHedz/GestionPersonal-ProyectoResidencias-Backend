package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.config.AppConstants.Pagination;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import com.chris.gestionpersonal.services.AssistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) String incidence,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Fetching assist details with pagination: page={}, size={}, sortBy={}, sortDirection={}, employeeId={}, incidence={}, startDate={}, endDate={}",
                page, size, sortBy, sortDirection, employeeId, incidence, startDate, endDate);
        Page<AssistDetailsDTO> assistDetailsPage = assistService.
                getAllAssistDetailsPaginated(page, size, sortBy, sortDirection, employeeId, incidence, startDate, endDate);

        return new ResponseEntity<>(assistDetailsPage, HttpStatus.OK);
    }

}