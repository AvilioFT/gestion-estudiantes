USE gestion_estudiantes;

-- Catálogo de carreras
INSERT INTO carreras (codigo, nombre, duracion_semestres, estado)
VALUES
('ING-SIS', 'Ingeniería de Sistemas', 10, 'ACTIVA'),
('ADMIN', 'Administración', 10, 'ACTIVA'),
('CONT', 'Contabilidad', 10, 'ACTIVA'),
('DERECHO', 'Derecho', 10, 'ACTIVA'),
('PSICO', 'Psicología', 10, 'ACTIVA')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    duracion_semestres = VALUES(duracion_semestres),
    estado = VALUES(estado);

-- Estudiantes de demostración
INSERT INTO estudiantes
(codigo, dni, nombres, apellidos, correo, telefono, fecha_nacimiento, estado)
VALUES
('EST0001', '76543210', 'Juan Carlos', 'Pérez López',
 'juan.perez@universidad.edu.pe', '987654321', '2004-05-12', 'ACTIVO'),

('EST0002', '70451283', 'María Fernanda', 'Quispe Huamán',
 'maria.quispe@universidad.edu.pe', '912345678', '2003-11-08', 'ACTIVO'),

('EST0003', '74812096', 'Diego Alonso', 'Ramírez Castro',
 'diego.ramirez@universidad.edu.pe', '934567812', '2004-02-19', 'ACTIVO'),

('EST0004', '71239845', 'Valeria Nicole', 'Flores Mendoza',
 'valeria.flores@universidad.edu.pe', '945678123', '2005-07-03', 'ACTIVO'),

('EST0005', '77903412', 'Sebastián André', 'Torres Salazar',
 'sebastian.torres@universidad.edu.pe', '956781234', '2002-09-27', 'ACTIVO'),

('EST0006', '72568130', 'Camila Alejandra', 'Rojas Paredes',
 'camila.rojas@universidad.edu.pe', '967812345', '2003-03-15', 'ACTIVO'),

('EST0007', '76021948', 'Mateo Gabriel', 'Vargas Ríos',
 'mateo.vargas@universidad.edu.pe', '978123456', '2004-12-01', 'ACTIVO'),

('EST0008', '73840517', 'Luciana Sofía', 'Herrera Campos',
 'luciana.herrera@universidad.edu.pe', '989234567', '2003-06-22', 'INACTIVO'),

('EST0009', '75190326', 'Alejandro Martín', 'Chávez Silva',
 'alejandro.chavez@universidad.edu.pe', '901345678', '2005-01-30', 'ACTIVO'),

('EST0010', '72954608', 'Daniela Fernanda', 'Ortiz Navarro',
 'daniela.ortiz@universidad.edu.pe', '913456789', '2004-08-14', 'INACTIVO')
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    correo = VALUES(correo),
    telefono = VALUES(telefono),
    fecha_nacimiento = VALUES(fecha_nacimiento),
    estado = VALUES(estado);

-- Matrículas del periodo actual
INSERT INTO matriculas
(estudiante_id, carrera_id, periodo, fecha_matricula, estado)
SELECT
    e.id,
    c.id,
    '2026-II',
    '2026-08-24',
    CASE WHEN e.estado = 'ACTIVO' THEN 'MATRICULADO' ELSE 'RETIRADO' END
FROM estudiantes e
JOIN carreras c ON c.codigo = CASE e.codigo
    WHEN 'EST0001' THEN 'ING-SIS'
    WHEN 'EST0002' THEN 'CONT'
    WHEN 'EST0003' THEN 'ING-SIS'
    WHEN 'EST0004' THEN 'ADMIN'
    WHEN 'EST0005' THEN 'ING-SIS'
    WHEN 'EST0006' THEN 'CONT'
    WHEN 'EST0007' THEN 'ADMIN'
    WHEN 'EST0008' THEN 'ING-SIS'
    WHEN 'EST0009' THEN 'DERECHO'
    WHEN 'EST0010' THEN 'PSICO'
END
WHERE e.codigo IN (
    'EST0001', 'EST0002', 'EST0003', 'EST0004', 'EST0005',
    'EST0006', 'EST0007', 'EST0008', 'EST0009', 'EST0010'
)
ON DUPLICATE KEY UPDATE
    carrera_id = VALUES(carrera_id),
    fecha_matricula = VALUES(fecha_matricula),
    estado = VALUES(estado);
