package com.chris.gestionpersonal.config;

import com.chris.gestionpersonal.repositories.EmployeeRepository;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@Configuration
public class SecurityBeansInjector {

    private EmployeeRepository employeeRepository;

    //AuthenticationManager es la interfaz principal para autenticar a un usuario.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //Crea un proveedor de autenticación el cual se conforma de un UserDetailsService y un PasswordEncoder
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    //* PasswordEncoder: Interfaz que encripta y desencripta contraseñas.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //* UserDetailsService: Interfaz que carga los datos de un usuario.
    @Bean
    public UserDetailsService userDetailsService(){
        return email -> employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("employee","email",email));
    }

}