package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.Repositories.TokenRepository;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.*;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Jwt;
import com.google.zxing.WriterException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final TokenRepository tokenRepository;
    private final QrCodeService qrCodeService;
    private final EmailService emailService;
    private static final String EMAIL = "email";

    public AuthResponse login(LoginDTO loginDTO, HttpServletResponse httpResponse ) {
        Authentication auth =  new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),loginDTO.getPassword()
        );
        authManager.authenticate(auth);
        Employee employee = employeeRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Employee",EMAIL, loginDTO.getEmail()));
        String jwt = jwtService.generateToken(employee,generateExtraClaims(employee));
        saveToken(jwt,employee);
        addJwtCookie(httpResponse,jwt);
        return employeeMapper.employeeToAuthResponse(employee);
    }

public AuthResponse register(RegisterDTO registerDTO, HttpServletResponse httpResponse) throws IOException, WriterException {
    File qrCodePath = qrCodeService.generateQRCode(registerDTO.getEmail(), 350, 350);
    Employee employee = employeeService.register(registerDTO);

    EmailDTO emailDTO = templateEmail(registerDTO.getEmail(),registerDTO.getFullName());

    emailService.sendEmail(emailDTO,qrCodePath);

    String jwt = jwtService.generateToken(employee, generateExtraClaims(employee));
    saveToken(jwt, employee);
    addJwtCookie(httpResponse, jwt);
    return employeeMapper.employeeToAuthResponse(employee);
}

    private void addJwtCookie(HttpServletResponse httpResponse, String jwt) {
        Cookie jwtCookie = new Cookie("jwt", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setAttribute("SameSite","Lax");
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60); // 1 hour in seconds (matching jwt.expiration.minutes)
        httpResponse.addCookie(jwtCookie);
    }

    public AuthResponse findLoggerUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken){
            throw new AuthenticationCredentialsNotFoundException("El usuario no esta autenticado ");
        }
        String employee =  auth.getName();
        log.info("Usuario autenticado 2: {}",employee);
        Employee authEmployee  = employeeRepository.findByEmail(employee).orElseThrow(()
                -> new ResourceNotFoundException("employee",EMAIL,employee));
        return employeeMapper.employeeToAuthResponse(authEmployee);
    }

    private Map<String,Object> generateExtraClaims(Employee employee) {
        return Map.of( EMAIL,employee.getEmail(),
                  "authorities",employee.getAuthorities()
        );
    }

    private void saveToken(String jwt,Employee employee) {
        Jwt token = new Jwt();
        token.setEmployee(employee);
        token.setToken(jwt);
        token.setExpiration(jwtService.extractExpiration(jwt));
        token.setValid(true);
        tokenRepository.save(token);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {

        Optional<String> jwt = jwtService.extractJwtFromCookie(request);
        if(jwt.isEmpty()){
            return;
        }
        Optional<Jwt> token = tokenRepository.findByToken(jwt.get());

        if(token.isPresent()  && token.get().isValid()){
            token.get().setValid(false);
            tokenRepository.save(token.get());
        }

        Cookie cookie = new Cookie("jwt", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expirar inmediatamente
        response.addCookie(cookie);
    }

private EmailDTO templateEmail(String email, String fullName) {
    EmailDTO emailDTO = new EmailDTO();
    emailDTO.setToUser(new String[]{email});
    emailDTO.setSubject("Bienvenido a GestionPersonal - Tu código QR de acceso");

    // Plantilla HTML mejorada
    String htmlTemplate =
        "<!DOCTYPE html>" +
        "<html lang='es'>" +
        "<head>" +
        "    <meta charset='UTF-8'>" +
        "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
        "    <title>Bienvenido a GestionPersonal</title>" +
        "</head>" +
        "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
        "    <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
        "        <div style='text-align: center; padding: 20px;'>" +
        "            <h1 style='color: #2c3e50; margin-bottom: 20px; border-bottom: 2px solid #3498db; padding-bottom: 10px;'>¡Bienvenido a GestionPersonal-Pasteleria Primavera</h1>" +
        "        </div>" +
        "        <div style='padding: 20px;'>" +
        "            <p style='font-size: 16px; color: #2c3e50; margin-bottom: 20px;'>Hola <strong style='color: #3498db;'>" + fullName + "</strong>,</p>" +
        "            <p style='font-size: 16px; color: #2c3e50; margin-bottom: 20px;'>Gracias por registrarte en nuestro sistema de gestión de personal. Estamos muy contentos de tenerte con nosotros.</p>" +
        "            <div style='background-color: #f9f9f9; border-left: 4px solid #3498db; padding: 15px; margin: 20px 0;'>" +
        "                <p style='font-size: 16px; color: #2c3e50; margin-bottom: 10px;'><strong>Tu código QR personal:</strong></p>" +
        "                <p style='font-size: 16px; color: #2c3e50; margin-bottom: 0;'>Adjunto encontrarás tu código QR de acceso personal. Puedes utilizarlo para registrar tu asistencia en el sistema.</p>" +
        "            </div>" +
        "            <p style='font-size: 16px; color: #2c3e50; margin-top: 30px;'>Si tienes alguna duda, no dudes en contactarnos.</p>" +
        "        </div>" +
        "        <div style='text-align: center; background-color: #2c3e50; color: white; padding: 15px; border-radius: 0 0 5px 5px;'>" +
        "            <p style='margin: 5px 0;'>GestionPersonal © " + java.time.Year.now().getValue() + "</p>" +
        "            <p style='margin: 5px 0; font-size: 12px;'>Sistema de Gestión de Recursos Humanos</p>" +
        "        </div>" +
        "    </div>" +
        "</body>" +
        "</html>";

    emailDTO.setMessage(htmlTemplate);
    return emailDTO;
}

}
