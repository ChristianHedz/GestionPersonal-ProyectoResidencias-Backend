CREATE EXTENSION IF NOT EXISTS unaccent;
-- Crear roles
INSERT INTO role (name) VALUES ('EMPLOYEE');
INSERT INTO role (name) VALUES ('ADMIN');

-- Crear un solo estado activo
INSERT INTO status (name) VALUES ('ACTIVO');

-- Insertar empleado admin con días de vacaciones
INSERT INTO employee (full_name, email, phone, password, role_id, status_id, available_vacation_days)
VALUES ('admin', 'admin@gmail.com', '9876543210',
        '$2a$10$bfXaNgJNPULCkyTemWhPCOIwdjw0/QnrNFTAaS1nUMqXzlmNSNWJy',
        (SELECT id FROM role WHERE name = 'ADMIN'),
        (SELECT id FROM status WHERE name = 'ACTIVO'),
        5); -- Valor inicial de días de vacaciones
 
INSERT INTO assist (date, entry_time, exit_time, incidents,reason, worked_hours, employee_id)
VALUES ('2025-06-04', '9:00:00', '17:00:00', 'FALTA',null,  8,
        (SELECT id FROM employee WHERE full_name = 'admin')),
       ('2025-06-05', '09:00:00', '17:00:00', 'RETARDO', 'problemas de salud',8,
        (SELECT id FROM employee WHERE full_name = 'admin')),
       ('2025-06-06', '09:00:00', '17:00:00', 'ASISTENCIA', null,8,
        (SELECT id FROM employee WHERE full_name = 'admin'));

-- Insertar 5 nuevos empleados con días de vacaciones
INSERT INTO employee (full_name, email, phone, password, role_id, status_id, available_vacation_days)
VALUES
    ('Juan Perez', 'cristian.28hedz@gmail.com', '5551234567',
     '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
     (SELECT id FROM role WHERE name = 'EMPLOYEE'),
     (SELECT id FROM status WHERE name = 'ACTIVO'),
     3), -- Valor inicial de días de vacaciones

    ('Maria Lopez', 'maria.lopez@gmail.com', '5552345678',
     '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
     (SELECT id FROM role WHERE name = 'EMPLOYEE'),
     (SELECT id FROM status WHERE name = 'ACTIVO'),
     5), -- Valor inicial de días de vacaciones

    ('Carlos Rodriguez', 'carlos.rodriguez@gmail.com', '5553456789',
     '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
     (SELECT id FROM role WHERE name = 'EMPLOYEE'),
     (SELECT id FROM status WHERE name = 'ACTIVO'),
     5), -- Valor inicial de días de vacaciones

    ('Ana Martinez', 'ana.martinez@gmail.com', '5554567890',
     '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
     (SELECT id FROM role WHERE name = 'EMPLOYEE'),
     (SELECT id FROM status WHERE name = 'ACTIVO'),
     4), -- Valor inicial de días de vacaciones

    ('Roberto Sanchez', 'roberto.sanchez@gmail.com', '5555678901',
     '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
     (SELECT id FROM role WHERE name = 'ADMIN'),
     (SELECT id FROM status WHERE name = 'ACTIVO'),
     2); -- Valor inicial de días de vacaciones

-- Insertar registros de asistencia para cada empleado (FALTA, RETARDO, ASISTENCIA)
-- (Las inserciones de asistencia no necesitan cambios)
INSERT INTO assist (date, entry_time, exit_time, incidents, reason, worked_hours, employee_id)
VALUES
    -- Juan Perez
    ('2025-07-10', NULL, NULL, 'FALTA', NULL, 0,
     (SELECT id FROM employee WHERE full_name = 'Juan Perez')),
    ('2025-07-11', '09:30:00', '17:30:00', 'RETARDO', 'Trafico', 8,
     (SELECT id FROM employee WHERE full_name = 'Juan Perez')),
    ('2025-07-12', '09:00:00', '17:00:00', 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Juan Perez')),
    ('2025-07-13', '09:20:00', '17:20:00', 'RETARDO', 'Lluvia intensa', 8,
     (SELECT id FROM employee WHERE full_name = 'Juan Perez')),
    ('2025-07-14', '09:30:00', '17:40:00', 'RETARDO', 'trafico', 8,
     (SELECT id FROM employee WHERE full_name = 'Juan Perez')),
    ('2025-07-15', '09:00:00', '17:00:00', 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Juan Perez')),

    -- Maria Lopez
    ('2025-07-10', NULL, NULL, 'FALTA', NULL, 0,
     (SELECT id FROM employee WHERE full_name = 'Maria Lopez')),
    ('2025-07-11', NULL, NULL, 'FALTA', NULL, 0,
     (SELECT id FROM employee WHERE full_name = 'Maria Lopez')),
    ('2025-07-12', '09:15:00', '17:15:00', 'RETARDO', 'Transporte público', 8,
     (SELECT id FROM employee WHERE full_name = 'Maria Lopez')),
    ('2025-07-13', '09:00:00', '17:00:00', 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Maria Lopez')),
    ('2025-07-14', '09:30:00', '17:40:00', 'RETARDO', 'trafico', 8,
     (SELECT id FROM employee WHERE full_name = 'Maria Lopez')),
    ('2025-07-15', '09:00:00', '17:00:00', 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Maria Lopez')),

    -- Carlos Rodriguez
    ('2025-07-11', NULL, NULL, 'FALTA', NULL, 0,
     (SELECT id FROM employee WHERE full_name = 'Carlos Rodriguez')),
    ('2025-07-12', NULL, NULL, 'FALTA', NULL, 0,
     (SELECT id FROM employee WHERE full_name = 'Carlos Rodriguez')),
    ('2025-07-13', '09:20:00', '17:20:00', 'RETARDO', 'Lluvia intensa', 8,
     (SELECT id FROM employee WHERE full_name = 'Carlos Rodriguez')),
    ('2025-07-14', '09:30:00', '17:40:00', 'RETARDO', 'trafico', 8,
     (SELECT id FROM employee WHERE full_name = 'Carlos Rodriguez')),
    ('2025-07-15', '09:00:00', '17:00:00', 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Carlos Rodriguez')),

    -- Ana Martinez
    ('2025-07-11', NULL, NULL, 'FALTA', NULL, 0,
     (SELECT id FROM employee WHERE full_name = 'Ana Martinez')),
    ('2025-07-12', '09:45:00', '17:45:00', 'RETARDO', 'Problema con vehículo', 8,
     (SELECT id FROM employee WHERE full_name = 'Ana Martinez')),
    ('2025-07-13', '09:00:00', '17:00:00', 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Ana Martinez')),
    ('2025-07-14', '09:30:00', '17:40:00', 'RETARDO', 'trafico', 8,
     (SELECT id FROM employee WHERE full_name = 'Ana Martinez')),
    ('2025-07-15', '09:00:00', '17:00:00', 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Ana Martinez')),

    -- Roberto Sanchez
    ('2025-07-11', NULL, NULL, 'FALTA', NULL, 0,
     (SELECT id FROM employee WHERE full_name = 'Roberto Sanchez')),
    ('2025-07-12', '09:10:00', '17:10:00', 'RETARDO', 'Accidente en la vía', 8,
     (SELECT id FROM employee WHERE full_name = 'Roberto Sanchez')),
    ('2025-07-13', '09:00:00', null, 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Roberto Sanchez')),
    ('2025-07-14', '09:30:00', '17:40:00', 'RETARDO', 'trafico', 8,
     (SELECT id FROM employee WHERE full_name = 'Roberto Sanchez')),
    ('2025-07-15', '09:00:00', '17:00:00', 'ASISTENCIA', null, 8,
     (SELECT id FROM employee WHERE full_name = 'Roberto Sanchez'));