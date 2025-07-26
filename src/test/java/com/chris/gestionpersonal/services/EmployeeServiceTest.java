package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.mapper.EmployeeMapper;
import com.chris.gestionpersonal.models.dto.EmployeeDTO;
import com.chris.gestionpersonal.models.dto.RegisterDTO;
import com.chris.gestionpersonal.models.dto.TokenGoogle;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.models.entity.Role;
import com.chris.gestionpersonal.models.entity.Status;
import com.chris.gestionpersonal.repositories.EmployeeRepository;
import com.chris.gestionpersonal.repositories.RoleRepository;
import com.chris.gestionpersonal.repositories.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.JsonFactory;

import java.util.Optional;
import java.util.List;
import java.util.Collections;
import com.chris.gestionpersonal.models.dto.AvailableVacationsDays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;

import com.chris.gestionpersonal.exceptions.EmailAlreadyRegisteredException;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;

import com.google.zxing.WriterException;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private QrCodeService qrCodeService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Captor
    private ArgumentCaptor<Employee> employeeCaptor;

    private Employee employee;
    private EmployeeDTO employeeDTO; 
    private Role employeeRole;
    private Status activeStatus;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFullName("John Doe");
        employee.setEmail("john.doe@example.com");

        // Creamos un DTO que corresponde al empleado
        employeeDTO = new EmployeeDTO();
        employeeDTO.setId(1L);
        employeeDTO.setFullName("John Doe");
        employeeDTO.setEmail("john.doe@example.com");
        
        employeeRole = new Role();
        employeeRole.setName("EMPLOYEE");
        
        activeStatus = new Status();
        activeStatus.setName("ACTIVO");
    }

    @Test
    @DisplayName("Debe devolver un EmployeeDTO cuando se busca por un ID existente")
    void cuandoBuscaPorIdExistente_entoncesDevuelveEmpleadoDTO() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.employeeToEmployeeDTO(employee)).thenReturn(employeeDTO);

        // Act
        EmployeeDTO foundDto = employeeService.getEmployeeById(1L);

        // Assert
        assertThat(foundDto).isNotNull();
        assertThat(foundDto.getId()).isEqualTo(1L);
        assertThat(foundDto.getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando se busca por un ID de DTO inexistente")
    void cuandoBuscaPorIdDtoInexistente_entoncesLanzaExcepcion() {
        // Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getEmployeeById(99L);
        });
    }

    @Test
    @DisplayName("Debe devolver un empleado cuando se busca por un email existente")
    void cuandoBuscaPorEmailExistente_entoncesDevuelveEmpleado() {
        // Arrange
        String email = "john.doe@example.com";
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));

        // Act
        Employee foundEmployee = employeeService.findByEmail(email);

        // Assert
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando se busca por un email inexistente")
    void cuandoBuscaPorEmailInexistente_entoncesLanzaExcepcion() {
        // Arrange
        String email = "nadie@example.com";
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.findByEmail(email);
        });
    }

    @Test
    @DisplayName("Debe registrar un nuevo empleado exitosamente")
    void cuandoRegistraNuevoEmpleado_entoncesSeGuardaCorrectamente() throws Exception {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFullName("John Doe");
        registerDTO.setEmail("john.doe@example.com");
        registerDTO.setPassword("password123");

        // Simulamos que el email no existe
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // Simulamos la devolución del mapper
        when(employeeMapper.registerDTOToEmployee(registerDTO)).thenReturn(employee);
        // Simulamos la codificación de la contraseña
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        // Simulamos la búsqueda de Role y Status
        when(roleRepository.findByName("EMPLOYEE")).thenReturn(Optional.of(employeeRole));
        when(statusRepository.findByName("ACTIVO")).thenReturn(Optional.of(activeStatus));
        // Simulamos la generación del QR y el guardado final
        when(qrCodeService.generateQRCode(anyString(), anyInt(), anyInt())).thenReturn(new java.io.File("qr.png"));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        Employee registeredEmployee = employeeService.register(registerDTO);

        // Assert
        assertThat(registeredEmployee).isNotNull();
        // Verificamos que se llamó a los componentes clave
        verify(passwordEncoder).encode("password123");
        verify(emailService).sendEmail(any(), any());
        verify(employeeRepository).save(employee);
    }
    
    @Test
    @DisplayName("Debe actualizar un empleado correctamente")
    void cuandoActualizaEmpleado_entoncesDevuelveDtoActualizado() {
        // Arrange
        EmployeeDTO updateRequest = new EmployeeDTO();
        updateRequest.setFullName("Johnathan Doe");
        updateRequest.setEmail("john.doe.new@example.com");
        updateRequest.setPhone("123456789");
        updateRequest.setStatus("INACTIVO");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(statusRepository.findByName("INACTIVO")).thenReturn(Optional.of(new Status()));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.employeeToEmployeeDTO(employee)).thenReturn(updateRequest);

        // Act
        EmployeeDTO updatedDto = employeeService.updateEmployee(1L, updateRequest);

        // Assert
        assertThat(updatedDto).isNotNull();
        verify(employeeRepository).save(employeeCaptor.capture());
        
        Employee capturedEmployee = employeeCaptor.getValue();
        assertThat(capturedEmployee.getFullName()).isEqualTo("Johnathan Doe");
        assertThat(capturedEmployee.getEmail()).isEqualTo("john.doe.new@example.com");
    }

    @Test
    @DisplayName("Debe actualizar la foto de un empleado si se proporciona")
    void cuandoActualizaEmpleadoConFoto_entoncesSeGuardaLaFoto() {
        // Arrange
        EmployeeDTO updateRequest = new EmployeeDTO();
        updateRequest.setPhoto("http://example.com/photo.jpg");
        // Necesitamos mockear el status tambien para que no falle
        updateRequest.setStatus("ACTIVO");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(statusRepository.findByName("ACTIVO")).thenReturn(Optional.of(activeStatus));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        employeeService.updateEmployee(1L, updateRequest);

        // Assert
        verify(employeeRepository).save(employeeCaptor.capture());
        assertThat(employeeCaptor.getValue().getPhoto()).isEqualTo("http://example.com/photo.jpg");
    }

    @Test
    @DisplayName("No debe generar QR para un usuario existente")
    void cuandoEnviaEmailConQrParaUsuarioExistente_noGeneraQr() throws IOException, WriterException {
        // Arrange
        String existingEmail = "john.doe@example.com";
        // Simulamos que el email SÍ existe
        when(employeeRepository.existsByEmail(existingEmail)).thenReturn(true);

        // Act
        employeeService.sendEmailWithQR(existingEmail, "John Doe");

        // Assert
        // Verificamos que el servicio de QR NUNCA fue llamado
        verify(qrCodeService, never()).generateQRCode(anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el estado no existe al actualizar")
    void cuandoActualizaConEstadoInexistente_entoncesLanzaExcepcion() {
        // Arrange
        EmployeeDTO updateRequest = new EmployeeDTO();
        updateRequest.setStatus("ESTADO_FALSO");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        // Simulamos que el Status no se encuentra
        when(statusRepository.findByName("ESTADO_FALSO")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.updateEmployee(1L, updateRequest);
        });

        // Verificamos que no se llegó a guardar
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Debe devolver una lista de todos los empleados")
    void cuandoListaTodos_entoncesDevuelveListaDto() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        // Le decimos al mapper que devuelva nuestro DTO de ejemplo bien formado
        when(employeeMapper.employeeListToEmployeeDTOList(anyList())).thenReturn(List.of(employeeDTO));

        // Act
        List<EmployeeDTO> employees = employeeService.listAllEmployees();

        // Assert
        assertThat(employees).isNotNull().isNotEmpty();
        // Añadimos una verificación más precisa
        assertThat(employees.get(0).getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si no hay empleados para listar")
    void cuandoListaTodosSinEmpleados_entoncesLanzaExcepcion() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        when(employeeMapper.employeeListToEmployeeDTOList(anyList())).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.listAllEmployees();
        });
    }
    
    @Test
    @DisplayName("Debe devolver la lista de días de vacaciones disponibles")
    void cuandoPideDiasDeVacaciones_entoncesDevuelveLista() {
        // Arrange
        AvailableVacationsDays vacationDays = new AvailableVacationsDays("John Doe", 15);
        List<AvailableVacationsDays> mockList = List.of(vacationDays);
        when(employeeRepository.findEmployeeFullNameAndAvailableVacationDays()).thenReturn(mockList);

        // Act
        List<AvailableVacationsDays> result = employeeService.getEmployeeAvailableVacationDay();

        // Assert
        assertThat(result).isNotNull().isEqualTo(mockList);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el rol por defecto no existe al registrar")
    void cuandoRegistraYRolNoExiste_entoncesLanzaExcepcion() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFullName("Test User");
        registerDTO.setEmail("test.user@example.com");
        registerDTO.setPassword("password");

        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(employeeMapper.registerDTOToEmployee(registerDTO)).thenReturn(new Employee());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        // Simulamos que el rol NO se encuentra
        when(roleRepository.findByName("EMPLOYEE")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.register(registerDTO);
        });
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el estado por defecto no existe al registrar")
    void cuandoRegistraYEstadoNoExiste_entoncesLanzaExcepcion() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFullName("Test User");
        registerDTO.setEmail("test.user@example.com");
        registerDTO.setPassword("password");

        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(employeeMapper.registerDTOToEmployee(registerDTO)).thenReturn(new Employee());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        // Simulamos que el ROL sí se encuentra...
        when(roleRepository.findByName("EMPLOYEE")).thenReturn(Optional.of(employeeRole));
        // ...pero el ESTADO NO.
        when(statusRepository.findByName("ACTIVO")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.register(registerDTO);
        });
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException si falla la generación del QR durante el registro")
    void cuandoFallaGeneracionQR_entoncesLanzaRuntimeException() throws IOException, WriterException {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFullName("Test User");
        registerDTO.setEmail("test.user@example.com");
        registerDTO.setPassword("password");

        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(employeeMapper.registerDTOToEmployee(registerDTO)).thenReturn(employee);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("EMPLOYEE")).thenReturn(Optional.of(employeeRole));
        when(statusRepository.findByName("ACTIVO")).thenReturn(Optional.of(activeStatus));
        // Simulamos el fallo en el servicio de QR
        when(qrCodeService.generateQRCode(anyString(), anyInt(), anyInt())).thenThrow(new IOException("Fallo de disco simulado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            employeeService.register(registerDTO);
        });
    }

    @Test
    @DisplayName("Debe lanzar EmailAlreadyRegisteredException si el email ya existe")
    void cuandoRegistraConEmailExistente_entoncesLanzaExcepcion() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFullName("Jane Doe");
        registerDTO.setEmail("jane.doe@example.com");
        registerDTO.setPassword("password123");
        
        // Simulamos que el email SÍ existe
        when(employeeRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(new Employee()));

        // Act & Assert
        assertThrows(EmailAlreadyRegisteredException.class, () -> {
            employeeService.register(registerDTO);
        });
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el payload de Google es nulo")
    void cuandoLoginGoogleConPayloadNulo_entoncesLanzaExcepcion() throws IOException {
        try (MockedStatic<GoogleIdToken> mockedStaticToken = setupGoogleTokenMock(null)) {
            TokenGoogle tokenGoogle = new TokenGoogle();
            tokenGoogle.setToken("fake-token");
            
            assertThrows(ResourceNotFoundException.class, () -> employeeService.loginGoogle(tokenGoogle));
        }
    }

    @Test
    @DisplayName("Debe devolver un empleado existente si ya está en la BD al loguear con Google")
    void cuandoLoginGoogleConUsuarioExistente_entoncesDevuelveEmpleadoExistente() throws IOException {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("john.doe@example.com");
        payload.set("name", "John Doe");
        
        try (MockedStatic<GoogleIdToken> mockedStaticToken = setupGoogleTokenMock(payload)) {
            TokenGoogle tokenGoogle = new TokenGoogle();
            tokenGoogle.setToken("fake-token");
            
            when(employeeRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(employee));

            Employee result = employeeService.loginGoogle(tokenGoogle);

            assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
            verify(employeeRepository, never()).save(any(Employee.class));
        }
    }

    @Test
    @DisplayName("Debe crear un nuevo empleado si no existe al loguear con Google")
    void cuandoLoginGoogleConUsuarioNuevo_entoncesCreaYDevuelveNuevoEmpleado() throws IOException {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("new.user@example.com");
        payload.set("name", "New User");

        try (MockedStatic<GoogleIdToken> mockedStaticToken = setupGoogleTokenMock(payload)) {
            String fakeSecretPassword = "test-secret-password";
            org.springframework.test.util.ReflectionTestUtils.setField(employeeService, "secretPsw", fakeSecretPassword);
            TokenGoogle tokenGoogle = new TokenGoogle();
            tokenGoogle.setToken("fake-token");

            when(employeeRepository.findByEmail("new.user@example.com")).thenReturn(Optional.empty());
            when(roleRepository.findByName("EMPLOYEE")).thenReturn(Optional.of(employeeRole));
            when(statusRepository.findByName("ACTIVO")).thenReturn(Optional.of(activeStatus));
            when(passwordEncoder.encode(fakeSecretPassword)).thenReturn("encodedSecretPassword");
            when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Employee result = employeeService.loginGoogle(tokenGoogle);

            assertThat(result.getEmail()).isEqualTo("new.user@example.com");
            verify(employeeRepository).save(any(Employee.class));
        }
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException en login con Google si el rol no existe")
    void cuandoLoginGoogleYRolNoExiste_entoncesLanzaExcepcion() throws IOException {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("new.user@example.com");
        payload.set("name", "New User");
        
        try (MockedStatic<GoogleIdToken> mockedStaticToken = setupGoogleTokenMock(payload)) {
            String fakeSecretPassword = "test-secret-password";
            org.springframework.test.util.ReflectionTestUtils.setField(employeeService, "secretPsw", fakeSecretPassword);
            TokenGoogle tokenGoogle = new TokenGoogle();
            tokenGoogle.setToken("fake-token");

            when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(fakeSecretPassword)).thenReturn("encodedSecretPassword");
            when(statusRepository.findByName("ACTIVO")).thenReturn(Optional.of(activeStatus));
            when(roleRepository.findByName("EMPLOYEE")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> employeeService.loginGoogle(tokenGoogle));
        }
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException en login con Google si el estado no existe")
    void cuandoLoginGoogleYEstadoNoExiste_entoncesLanzaExcepcion() throws IOException {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("new.user@example.com");
        payload.set("name", "New User");

        try (MockedStatic<GoogleIdToken> mockedStaticToken = setupGoogleTokenMock(payload)) {
            String fakeSecretPassword = "test-secret-password";
            org.springframework.test.util.ReflectionTestUtils.setField(employeeService, "secretPsw", fakeSecretPassword);
            TokenGoogle tokenGoogle = new TokenGoogle();
            tokenGoogle.setToken("fake-token");

            when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(fakeSecretPassword)).thenReturn("encodedSecretPassword");
            when(statusRepository.findByName("ACTIVO")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> employeeService.loginGoogle(tokenGoogle));
        }
    }
    
    private MockedStatic<GoogleIdToken> setupGoogleTokenMock(GoogleIdToken.Payload payload) throws IOException {
        GoogleIdToken mockIdToken = Mockito.mock(GoogleIdToken.class);
        MockedStatic<GoogleIdToken> mockedStaticToken = Mockito.mockStatic(GoogleIdToken.class);
        
        mockedStaticToken.when(() -> GoogleIdToken.parse(any(JsonFactory.class), anyString())).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(payload);
        
        return mockedStaticToken;
    }
} 