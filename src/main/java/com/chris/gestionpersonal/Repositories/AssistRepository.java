package com.chris.gestionpersonal.Repositories;

import com.chris.gestionpersonal.models.entity.Assist;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssistRepository extends CrudRepository<Assist, Long>, JpaSpecificationExecutor<Assist> {
    List<Assist> findByDate(LocalDate date);
}