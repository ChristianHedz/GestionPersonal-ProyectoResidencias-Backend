package com.chris.gestionpersonal.Repositories;

import com.chris.gestionpersonal.models.entity.Assist;
import com.chris.gestionpersonal.models.entity.Employee;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssistRepository extends CrudRepository<Assist, Long> {
    List<Assist> findByDate(LocalDate date);
}