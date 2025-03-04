package com.chris.gestionpersonal.models.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String qrCode;
    private String photo;
    private String password;
    @OneToOne(cascade = CascadeType.ALL,fetch =  FetchType.EAGER)
    private Status status;
    @OneToOne(cascade = CascadeType.ALL,fetch =  FetchType.EAGER)
    private Role role;
    @OneToMany(cascade = CascadeType.ALL,fetch =  FetchType.LAZY)
    private List<Assists> assists;

}
