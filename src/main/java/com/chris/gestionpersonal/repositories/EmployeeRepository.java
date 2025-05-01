package com.chris.gestionpersonal.repositories;

import com.chris.gestionpersonal.models.dto.AvailableVacationsDays;
import com.chris.gestionpersonal.models.entity.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findById(Long id);
    List<Employee> findByStatus_Name(String statusName);
    boolean existsByEmail(String email);
    @Query("SELECT NEW com.chris.gestionpersonal.models.dto.AvailableVacationsDays(e.fullName, e.availableVacationDays) FROM Employee e")
    List<AvailableVacationsDays> findEmployeeFullNameAndAvailableVacationDays();
}
