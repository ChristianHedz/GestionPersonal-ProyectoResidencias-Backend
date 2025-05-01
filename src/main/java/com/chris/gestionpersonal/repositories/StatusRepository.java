package com.chris.gestionpersonal.repositories;

import com.chris.gestionpersonal.models.entity.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StatusRepository  extends CrudRepository<Status,Long> {
    Optional<Status> findByName(String name);
}
