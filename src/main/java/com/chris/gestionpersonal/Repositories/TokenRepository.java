package com.chris.gestionpersonal.Repositories;

import com.chris.gestionpersonal.models.entity.Jwt;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Jwt, Long> {
    Optional<Jwt> findByToken(String token);
}
