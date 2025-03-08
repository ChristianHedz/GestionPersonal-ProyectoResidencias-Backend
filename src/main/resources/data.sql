-- Crear roles
INSERT INTO role (name) VALUES ('EMPLEADO');
INSERT INTO role (name) VALUES ('ADMINISTRADOR');

-- Crear un solo estado activo
INSERT INTO status (status_name) VALUES ('ACTIVO');

-- Insertar empleados con el mismo estado
INSERT INTO employee (full_name, email, phone, password, role_id, status_id)
VALUES ('chris', 'chris@gmail.com', '1234567890',
        '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
        (SELECT id FROM role WHERE name = 'EMPLEADO'),
        (SELECT id FROM status WHERE status_name = 'ACTIVO'));

INSERT INTO employee (full_name, email, phone, password, role_id, status_id)
VALUES ('admin', 'admin@gmail.com', '9876543210',
        '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
        (SELECT id FROM role WHERE name = 'ADMINISTRADOR'),
        (SELECT id FROM status WHERE status_name = 'ACTIVO'));