package com.chris.gestionpersonal.models.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
public class Employee implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String qrCode;
    @Column(length = 1000)
    private String photo;
    private String password;
    private Integer availableVacationDays;
    @ManyToOne
    @JoinColumn(name = "status_id",nullable = false)
    private Status status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @OneToMany(cascade = CascadeType.ALL,fetch =  FetchType.LAZY)
    private List<Assist> assists;
    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    private Set<CalendarEvent> events = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
