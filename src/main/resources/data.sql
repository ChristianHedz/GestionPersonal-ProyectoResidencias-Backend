-- Crear roles
INSERT INTO role (name) VALUES ('EMPLOYEE');
INSERT INTO role (name) VALUES ('ADMIN');

-- Crear un solo estado activo
INSERT INTO status (name) VALUES ('ACTIVO');

INSERT INTO employee (full_name, email, phone, password, role_id, status_id)
VALUES ('admin', 'admin@gmail.com', '9876543210',
        '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
        (SELECT id FROM role WHERE name = 'ADMIN'),
        (SELECT id FROM status WHERE name = 'ACTIVO'));

INSERT INTO assist (date, entry_time, exit_time, incidents, worked_hours, employee_id)
VALUES ('2025-04-04', '08:00:00', '17:00:00', 'FALTA', 9,
        (SELECT id FROM employee WHERE full_name = 'admin')),
       ('2025-04-04', '08:00:00', '17:00:00', 'RETARDO', 9,
        (SELECT id FROM employee WHERE full_name = 'admin')),
       ('2025-04-04', '08:00:00', '17:00:00', 'ASISTENCIA', 9,
        (SELECT id FROM employee WHERE full_name = 'admin'));
