package com.chris.gestionpersonal.repositories;

import com.chris.gestionpersonal.models.entity.Assist;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AssistSpecifications {
    public static Specification<Assist> withEmployeeId(Long employeeId) {
        return (root, query, cb)
                -> employeeId == null ? null : cb.equal(root.get("employee").get("id"), employeeId);
    }

    public static Specification<Assist> withIncidence(String incidence) {
        return (root, query, cb)
                -> incidence == null ? null : cb.equal(root.get("incidents"), incidence);
    }

    public static Specification<Assist> withDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null || endDate == null) return null;
            return cb.between(root.get("date"), startDate, endDate);
        };
    }
}