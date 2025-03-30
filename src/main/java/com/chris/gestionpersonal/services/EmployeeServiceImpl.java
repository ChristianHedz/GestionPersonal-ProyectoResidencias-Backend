package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.Repositories.AssistRepository;
import com.chris.gestionpersonal.Repositories.EmployeeRepository;
import com.chris.gestionpersonal.Repositories.RoleRepository;
import com.chris.gestionpersonal.Repositories.StatusRepository;
import com.chris.gestionpersonal.exceptions.EmailAlreadyRegisteredException;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.*;
import com.chris.gestionpersonal.models.entity.Assist;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Role;
import com.chris.gestionpersonal.models.entity.Status;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements  EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final QrCodeService qrCodeService;

    @Value("${google.clientId}")
    private String googleClientId;
    @Value("${secretPsw}")
    private String secretPsw;


    public Employee register(RegisterDTO registerDTO) {
        validateEmailAvailability(registerDTO.getEmail());
        Employee employee = employeeMapper.registerDTOToEmployee(registerDTO);
        employee.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        Role role = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new ResourceNotFoundException("role","name","ROLE_EMPLOYEE"));
        Status status = statusRepository.findByName("ACTIVO")
                .orElseThrow(() -> new ResourceNotFoundException("status","name","ACTIVO"));
        employee.setRole(role);
        employee.setStatus(status);
        sendEmailWithQR(registerDTO.getEmail(),registerDTO.getFullName());
        return employeeRepository.save(employee);
    }

    @Override
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("employee","email",email));
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("employee","id",id));
    }

    private void validateEmailAvailability(String email){
        employeeRepository.findByEmail(email)
                .ifPresent(employee -> {
                    throw new EmailAlreadyRegisteredException("El email ya se encuentra registrado");
                });
    }


    public void sendEmailWithQR(String email,String fullName) {
        boolean isNewUser = !employeeRepository.existsByEmail(email);
        if(isNewUser) {
            try {
                File qrCodePath = qrCodeService.generateQRCode(email, 350, 350);
                EmailDTO emailDTO = emailService.templateEmail(email,fullName);
                emailService.sendEmail(emailDTO,qrCodePath);
            } catch (IOException | WriterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<EmployeeDTO> listAllEmployees() {
        List<EmployeeDTO> employeeList = employeeMapper.employeeListToEmployeeDTOList(employeeRepository.findAll());
        if (employeeList.isEmpty()){
            throw new ResourceNotFoundException("Employees");
        }
        return employeeList;
    }

    public EmployeeDTO updateEmployee(Long idEmpleado,EmployeeDTO employeeDTO) {
        Employee employee = this.findById(idEmpleado);
        employee.setFullName(employeeDTO.getFullName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhone(employeeDTO.getPhone());
        Status status = statusRepository.findByName(employeeDTO.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("status","name",employeeDTO.getStatus()));
        employee.setStatus(status);
        employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeDTO(employee);
    }

    @Override
    public Employee loginGoogle(TokenGoogle tokenDto) throws IOException {
        final NetHttpTransport transport = new NetHttpTransport();
        final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId));

        GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), tokenDto.getToken());
        final GoogleIdToken.Payload payload = googleIdToken.getPayload();

        if (payload == null) {
            throw new ResourceNotFoundException("Google authentication", "payload", "null");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        return employeeRepository.findByEmail(email)
                .orElseGet(() -> {
                    Employee newEmployee = new Employee();
                    newEmployee.setFullName(name);
                    newEmployee.setEmail(email);
                    newEmployee.setPhoto(pictureUrl);
                    newEmployee.setPassword(passwordEncoder.encode(secretPsw));
                    newEmployee.setStatus(statusRepository.findByName("ACTIVO")
                            .orElseThrow(() -> new ResourceNotFoundException("status", "name", "ACTIVO")));
                    newEmployee.setRole(roleRepository.findByName("EMPLOYEE")
                            .orElseThrow(() -> new ResourceNotFoundException("role", "name", "EMPLOYEE")));
                    sendEmailWithQR(email,name);
                    return employeeRepository.save(newEmployee);
                });
    }

}
