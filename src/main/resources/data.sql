-- Crear roles
INSERT INTO role (name) VALUES ('ROLE_EMPLOYEE');
INSERT INTO role (name) VALUES ('ROLE_ADMIN');

-- Crear un solo estado activo
INSERT INTO status (name) VALUES ('ACTIVO');

INSERT INTO employee (full_name, email, phone, password, role_id, status_id)
VALUES ('admin', 'admin@gmail.com', '9876543210',
        '$2a$10$ywh1O2EwghHmFIMGeHgsx.9lMw5IXpg4jafeFS.Oi6nFv0181gHli',
        (SELECT id FROM role WHERE name = 'ROLE_ADMIN'),
        (SELECT id FROM status WHERE name = 'ACTIVO'));