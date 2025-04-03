package com.chris.gestionpersonal.Repositories;

import com.chris.gestionpersonal.models.entity.Assist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssistRepository extends CrudRepository<Assist, Long> {
    List<Assist> findByDate(LocalDate date);
    Page<Assist> findAllByOrderByDateDescEntryTimeDesc(Pageable pageable);
}