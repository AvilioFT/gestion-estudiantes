USE gestion_estudiantes;

INSERT IGNORE INTO carreras (codigo, nombre, duracion_semestres, estado)
VALUES
('ING-SIS', 'Ingeniería de Sistemas', 10, 'ACTIVA'),
('ADMIN', 'Administración', 10, 'ACTIVA'),
('CONT', 'Contabilidad', 10, 'ACTIVA');

INSERT IGNORE INTO estudiantes
(codigo, dni, nombres, apellidos, correo, telefono, fecha_nacimiento, estado)
VALUES
('EST0001', '76543210', 'Juan Carlos', 'Pérez López',
 'juan.carlos@example.com', '987654321', '2004-05-12', 'ACTIVO');

INSERT IGNORE INTO matriculas
(estudiante_id, carrera_id, periodo, fecha_matricula, estado)
SELECT e.id, c.id, '2026-II', CURRENT_DATE, 'MATRICULADO'
FROM estudiantes e
JOIN carreras c ON c.codigo = 'ING-SIS'
WHERE e.codigo = 'EST0001';
