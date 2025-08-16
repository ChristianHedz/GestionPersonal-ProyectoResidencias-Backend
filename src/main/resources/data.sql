CREATE EXTENSION IF NOT EXISTS unaccent;
-- Crear roles
INSERT INTO role (name) VALUES ('EMPLOYEE');
INSERT INTO role (name) VALUES ('ADMIN');

-- Crear un solo estado activo
INSERT INTO status (name) VALUES ('ACTIVO');

-- Insertar empleado admin con días de vacaciones
INSERT INTO employee (full_name, email, phone, password, role_id, status_id, available_vacation_days)
VALUES ('Admin admin', 'admin@gmail.com', '9876543210',
        '$2a$10$bfXaNgJNPULCkyTemWhPCOIwdjw0/QnrNFTAaS1nUMqXzlmNSNWJy',
        (SELECT id FROM role WHERE name = 'ADMIN'),
        (SELECT id FROM status WHERE name = 'ACTIVO'),
        5); -- Valor inicial de días de vacaciones

