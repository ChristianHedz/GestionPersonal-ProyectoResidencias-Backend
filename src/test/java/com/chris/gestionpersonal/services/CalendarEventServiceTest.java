package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.exceptions.EventServiceException;
import com.chris.gestionpersonal.exceptions.ResourceNotFoundException;
import com.chris.gestionpersonal.mapper.CalendarEventMapper;
import com.chris.gestionpersonal.models.dto.CalendarEventDTO;
import com.chris.gestionpersonal.models.entity.CalendarEvent;
import com.chris.gestionpersonal.models.entity.Employee;
import com.chris.gestionpersonal.repositories.CalendarEventRepository;
import com.chris.gestionpersonal.repositories.EmployeeRepository;
import com.chris.gestionpersonal.services.impl.CalendarEventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CalendarEventMapper calendarEventMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CalendarEventServiceImpl calendarEventService;

    @Captor
    private ArgumentCaptor<CalendarEvent> eventCaptor;

    private CalendarEventDTO eventDTO;
    private CalendarEvent event;
    private Employee employee;

    @BeforeEach
    void setUp() {
        eventDTO = new CalendarEventDTO();
        eventDTO.setTitle("Reunión de equipo");
        eventDTO.setDescription("Discutir el progreso del sprint.");

        event = new CalendarEvent();
        event.setId(1L);
        event.setTitle("Reunión de equipo");
        event.setDescription("Discutir el progreso del sprint.");

        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("test@example.com");
        employee.setFullName("John Doe");
    }

    @Test
    @DisplayName("Debe crear y devolver un nuevo evento correctamente")
    void cuandoCreaEvento_entoncesDebeDevolverEventoGuardado() {
        // 1. Arrange (Organizar el guion de los actores)
        when(calendarEventMapper.toEntity(eventDTO)).thenReturn(event);
        when(calendarEventRepository.save(any(CalendarEvent.class))).thenReturn(event);
        when(calendarEventMapper.toDTO(event)).thenReturn(eventDTO);

        // 2. Act
        CalendarEventDTO savedEventDTO = calendarEventService.createEvent(eventDTO);

        // 3. Assert
        assertThat(savedEventDTO).isNotNull();
        assertThat(savedEventDTO.getTitle()).isEqualTo(eventDTO.getTitle());

        verify(calendarEventRepository).save(any(CalendarEvent.class));
    }

    @Test
    @DisplayName("Debe crear un evento, asignarlo a empleados y enviar emails")
    void cuandoCreaEventoConEmpleados_entoncesEnviaEmail() {
        // 1. Arrange
        eventDTO.setEmployeeIds(Set.of(1L));
        event.setEmployees(Set.of(employee));

        when(calendarEventMapper.toEntity(eventDTO)).thenReturn(event);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(calendarEventRepository.save(any(CalendarEvent.class))).thenReturn(event);
        when(calendarEventMapper.toDTO(event)).thenReturn(eventDTO);

        // 2. Act
        calendarEventService.createEvent(eventDTO);

        // 3. Assert
        verify(emailService).sendBatchEventEmails(any(), any(CalendarEvent.class));
        verify(employeeRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe devolver un evento cuando se busca por un ID existente")
    void cuandoBuscaPorIdExistente_entoncesDevuelveEvento() {
        // 1. Arrange
        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(calendarEventMapper.toDTO(event)).thenReturn(eventDTO);

        // 2. Act
        CalendarEventDTO foundEvent = calendarEventService.getEventById(1L);

        // 3. Assert
        assertThat(foundEvent).isNotNull();
        assertThat(foundEvent.getTitle()).isEqualTo(event.getTitle());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando se busca por un ID inexistente")
    void cuandoBuscaPorIdInexistente_entoncesLanzaExcepcion() {
        // 1. Arrange
        long idInexistente = 99L;
        when(calendarEventRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        assertThrows(ResourceNotFoundException.class, () -> calendarEventService.getEventById(idInexistente));
    }

    @Test
    @DisplayName("Debe actualizar un evento existente correctamente")
    void cuandoActualizaEvento_entoncesSeGuardaConNuevosDatos() {
        // 1. Arrange
        CalendarEventDTO updatedDto = new CalendarEventDTO();
        updatedDto.setTitle("Reunión de equipo ACTUALIZADA");
        updatedDto.setDescription("Nueva descripción.");

        when(calendarEventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(calendarEventRepository.save(any(CalendarEvent.class))).thenReturn(event);

        // 2. Act
        calendarEventService.updateEvent(1L, updatedDto);

        // 3. Assert
        // Capturamos el argumento pasado a 'save' para una verificación precisa.
        verify(calendarEventRepository).save(eventCaptor.capture());
        CalendarEvent capturedEvent = eventCaptor.getValue();

        // Verificamos que las propiedades del objeto capturado son las correctas.
        assertThat(capturedEvent.getTitle()).isEqualTo("Reunión de equipo ACTUALIZADA");
        assertThat(capturedEvent.getDescription()).isEqualTo("Nueva descripción.");
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException al intentar actualizar un evento que no existe")
    void cuandoActualizaEventoInexistente_entoncesLanzaExcepcion() {
        // 1. Arrange
        long idInexistente = 99L;
        when(calendarEventRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // 2. Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> calendarEventService.updateEvent(idInexistente, eventDTO));

        // Verificamos que nunca se intentó guardar nada
        verify(calendarEventRepository, never()).save(any(CalendarEvent.class));
    }

    @Test
    @DisplayName("Debe eliminar un evento cuando el ID existe")
    void cuandoEliminaEventoExistente_entoncesNoDebeLanzarExcepcion() {
        // 1. Arrange
        long eventId = 1L;
        when(calendarEventRepository.existsById(eventId)).thenReturn(true);

        // 2. Act
        calendarEventService.deleteEvent(eventId);

        // 3. Assert
        verify(calendarEventRepository).deleteById(eventId);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException al intentar eliminar un evento que no existe")
    void cuandoIntentaEliminarEventoInexistente_entoncesLanzaExcepcion() {
        // 1. Arrange
        long idInexistente = 99L;
        when(calendarEventRepository.existsById(idInexistente)).thenReturn(false);

        // 2. Act & 3. Assert
        assertThrows(ResourceNotFoundException.class, () -> calendarEventService.deleteEvent(idInexistente));
    }

    @Test
    @DisplayName("Debe devolver una lista de todos los eventos")
    void cuandoPideTodosLosEventos_entoncesDevuelveLista() {
        // 1. Arrange
        when(calendarEventRepository.findAll()).thenReturn(List.of(event));
        when(calendarEventMapper.toDTO(event)).thenReturn(eventDTO);

        // 2. Act
        List<CalendarEventDTO> events = calendarEventService.getAllEvents();

        // 3. Assert
        assertThat(events).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Debe lanzar una EventServiceException si el repositorio falla al obtener todos los eventos")
    void cuandoRepositorioFallaEnFindAll_entoncesLanzaEventServiceException() {
        // 1. Arrange
        when(calendarEventRepository.findAll()).thenThrow(new RuntimeException("Error de base de datos simulado"));

        // 2. Act & Assert
        assertThrows(EventServiceException.class, () -> calendarEventService.getAllEvents());
    }

    @Test
    @DisplayName("Debe devolver los eventos de un empleado específico")
    void cuandoBuscaEventosPorEmpleado_entoncesDevuelveListaCorrecta() {
        // 1. Arrange
        long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(calendarEventRepository.findByEmployeesContaining(employee)).thenReturn(List.of(event));
        when(calendarEventMapper.toDTO(event)).thenReturn(eventDTO);

        // 2. Act
        List<CalendarEventDTO> employeeEvents = calendarEventService.getEventsByEmployeeId(employeeId);

        // 3. Assert
        assertThat(employeeEvents).isNotNull().hasSize(1);
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    @DisplayName("Debe devolver eventos dentro de un rango de fechas")
    void cuandoBuscaPorRangoDeFechas_entoncesDevuelveEventos() {
        // 1. Arrange
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusDays(1);
        when(calendarEventRepository.findByStartDateBetween(inicio, fin)).thenReturn(List.of(event));

        // 2. Act
        List<CalendarEventDTO> events = calendarEventService.getEventsByDateRange(inicio, fin);

        // 3. Assert
        assertThat(events).isNotEmpty();
        verify(calendarEventRepository).findByStartDateBetween(inicio, fin);
    }

    @Test
    @DisplayName("Debe devolver eventos de un empleado en un rango de fechas")
    void cuandoBuscaPorEmpleadoYFechas_entoncesDevuelveEventos() {
        // 1. Arrange
        long employeeId = 1L;
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusDays(1);
        when(calendarEventRepository.findByEmployeeIdAndDateRange(employeeId, inicio, fin)).thenReturn(List.of(event));

        // 2. Act
        List<CalendarEventDTO> events = calendarEventService.getEventsByEmployeeIdAndDateRange(employeeId, inicio, fin);

        // 3. Assert
        assertThat(events).isNotEmpty();
        verify(calendarEventRepository).findByEmployeeIdAndDateRange(employeeId, inicio, fin);
    }
} 