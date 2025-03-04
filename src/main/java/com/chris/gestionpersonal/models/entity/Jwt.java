package com.chris.gestionpersonal.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tokens")
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expiration;
    private boolean isValid;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
