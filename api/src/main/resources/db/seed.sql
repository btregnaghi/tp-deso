-- Datos de prueba para todos los CU
-- Uso (despues de schema.sql):
--   docker exec -i evaluaciones-db psql -U postgres -d evaluaciones < api/src/main/resources/db/seed.sql
-- Se puede correr de nuevo: vacia las tablas y vuelve a cargar todo.
--
-- Casos que cubre:
--   * Competencias evaluables: COMP01 a COMP04 (todos sus factores activos tienen 2 o mas preguntas activas).
--   * Competencias NO evaluables (CU025 paso 9):
--       COMP05 Liderazgo -> sus factores tienen 1 sola pregunta activa (otra fue dada de baja)
--       COMP06 Redes     -> su factor no tiene preguntas
--       COMP07 Testing   -> su unico factor esta dado de baja
--       COMP08 COBOL     -> la competencia esta dada de baja
--   * Puestos evaluables: DEV01, ANA01, DBA01. No evaluables: LID01, QA01, SOP01.
--   * Candidatos con cuestionario vigente (bloqueados en CU025): Silva y Diaz (ACTIVO), Acosta (EN_PROCESO).
--   * Candidato eliminado: Herrera (no aparece en las busquedas).
--   * Libres para evaluar: Benitez, Castro, Sosa, Romero y todos los que ya completaron.
--   * Orden de merito (CU028): evaluaciones de DEV01 y ANA01 con cuestionarios COMPLETADO,
--     uno INCOMPLETO y uno SIN_CONTESTAR.
--   * Login de candidato (CU001) y completar cuestionario (CU026): ver las claves en la seccion de cuestionarios.

BEGIN;

TRUNCATE registro_auditoria, cuestionario, evaluacion, candidato, caracteristica_puesto,
         puesto, opcion, pregunta, factor, competencia, empresa
RESTART IDENTITY CASCADE;

-- =====================================================================
-- Empresas
-- =====================================================================
INSERT INTO empresa (id, nombre) VALUES
    (1, 'Sistemas del Litoral S.A.'),
    (2, 'Banco Santafesino'),
    (3, 'Logística Paraná S.R.L.');

-- =====================================================================
-- Competencias
-- =====================================================================
INSERT INTO competencia (id, codigo, nombre, descripcion, tipo, fecha_baja) VALUES
    (1, 'COMP01', 'Programación Java', 'Dominio del lenguaje Java y de la programación orientada a objetos', 'TECNICA', NULL),
    (2, 'COMP02', 'Bases de datos', 'Consultas SQL y modelado de datos relacional', 'TECNICA', NULL),
    (3, 'COMP03', 'Trabajo en equipo', 'Capacidad para colaborar y resolver conflictos con otros', 'ACTITUDINAL', NULL),
    (4, 'COMP04', 'Comunicación', 'Claridad para transmitir ideas en forma oral y escrita', 'ACTITUDINAL', NULL),
    (5, 'COMP05', 'Liderazgo', 'Capacidad para guiar y motivar a un equipo', 'ACTITUDINAL', NULL),
    (6, 'COMP06', 'Redes', 'Conocimientos de protocolos y configuración de redes', 'TECNICA', NULL),
    (7, 'COMP07', 'Testing', 'Diseño y ejecución de pruebas de software', 'TECNICA', NULL),
    (8, 'COMP08', 'COBOL', 'Programación en COBOL para sistemas heredados', 'TECNICA', '2026-09-15');

-- =====================================================================
-- Factores
-- =====================================================================
INSERT INTO factor (id, codigo, nombre, descripcion, nro_orden, fecha_baja, competencia_id) VALUES
    (1,  'COMP01-F01', 'Programación orientada a objetos', 'Clases, herencia, interfaces y polimorfismo', 1, NULL, 1),
    (2,  'COMP01-F02', 'Manejo de excepciones', 'Excepciones checked y unchecked, try/catch/finally', 2, NULL, 1),
    (3,  'COMP02-F01', 'Lenguaje SQL', 'Consultas, joins y funciones de agregación', 1, NULL, 2),
    (4,  'COMP02-F02', 'Modelado de datos', 'Normalización y diseño de relaciones', 2, NULL, 2),
    (5,  'COMP03-F01', 'Colaboración', 'Disposición a ayudar y compartir información', 1, NULL, 3),
    (6,  'COMP03-F02', 'Resolución de conflictos', 'Manejo de desacuerdos dentro del equipo', 2, NULL, 3),
    (7,  'COMP04-F01', 'Comunicación oral', 'Presentaciones y escucha activa', 1, NULL, 4),
    (8,  'COMP04-F02', 'Comunicación escrita', 'Redacción de correos e informes', 2, NULL, 4),
    (9,  'COMP05-F01', 'Toma de decisiones', 'Decidir con información incompleta', 1, NULL, 5),
    (10, 'COMP05-F02', 'Motivación del equipo', 'Reconocer y motivar a los demás', 2, NULL, 5),
    (11, 'COMP06-F01', 'Protocolos de red', 'TCP/IP, DNS y DHCP', 1, NULL, 6),
    (12, 'COMP07-F01', 'Pruebas unitarias', 'Pruebas automatizadas con mocks', 1, '2026-10-01', 7),
    (13, 'COMP08-F01', 'Sintaxis COBOL', 'Divisiones y cláusulas del lenguaje', 1, NULL, 8);

-- =====================================================================
-- Preguntas
-- Las ponderaciones de las opciones de cada pregunta suman 10.
-- =====================================================================
INSERT INTO pregunta (id, version, nombre, texto, descripcion, tipo, fecha_modificacion, fecha_baja, factor_id) VALUES
    -- Factor 1: Programación orientada a objetos (6 activas + 1 dada de baja)
    (1,  1, 'Encapsulamiento', '¿Qué principio de la programación orientada a objetos oculta el estado interno de un objeto y solo lo expone a través de sus métodos?', NULL, 'OPCION_SIMPLE', '2026-10-01 09:00', NULL, 1),
    (2,  1, 'Herencia múltiple', 'En Java una clase puede extender a más de una clase.', 'Verdadero o falso', 'OPCION_SIMPLE', '2026-10-01 09:05', NULL, 1),
    (3,  1, 'Interfaces', '¿Cuáles de las siguientes afirmaciones sobre las interfaces de Java son correctas?', 'Hay dos respuestas correctas', 'OPCION_MULTIPLE', '2026-10-01 09:10', NULL, 1),
    (4,  1, 'Polimorfismo', '¿Qué permite el polimorfismo?', NULL, 'OPCION_SIMPLE', '2026-10-01 09:15', NULL, 1),
    (5,  1, 'Sobrecarga de métodos', '¿Qué distingue a dos métodos sobrecargados de una misma clase?', NULL, 'OPCION_SIMPLE', '2026-10-01 09:20', NULL, 1),
    (6,  1, 'Clases abstractas', 'Una clase abstracta se puede instanciar con new.', 'Verdadero o falso', 'OPCION_SIMPLE', '2026-10-01 09:25', NULL, 1),
    (33, 1, 'Punteros en Java', 'Java permite hacer aritmética de punteros.', 'Verdadero o falso', 'OPCION_SIMPLE', '2026-10-01 09:30', '2026-10-20 18:00', 1),
    -- Factor 2: Manejo de excepciones
    (7,  1, 'Checked exceptions', '¿Cuál de estas excepciones es checked?', NULL, 'OPCION_SIMPLE', '2026-10-02 10:00', NULL, 2),
    (8,  1, 'Bloque finally', 'El bloque finally se ejecuta aunque el try termine con un return.', 'Verdadero o falso', 'OPCION_SIMPLE', '2026-10-02 10:05', NULL, 2),
    (9,  1, 'Try-with-resources', '¿Qué afirmaciones sobre try-with-resources son correctas?', 'Hay dos respuestas correctas', 'OPCION_MULTIPLE', '2026-10-02 10:10', NULL, 2),
    -- Factor 3: Lenguaje SQL
    (10, 1, 'Inner join', '¿Qué filas devuelve un INNER JOIN entre dos tablas?', NULL, 'OPCION_SIMPLE', '2026-10-03 11:00', NULL, 3),
    (11, 1, 'Cláusula HAVING', '¿Para qué se usa la cláusula HAVING?', NULL, 'OPCION_SIMPLE', '2026-10-03 11:05', NULL, 3),
    (12, 1, 'Claves foráneas', 'Una clave foránea puede referenciar a la misma tabla en la que está definida.', 'Verdadero o falso', 'OPCION_SIMPLE', '2026-10-03 11:10', NULL, 3),
    (13, 1, 'Funciones de agregación', '¿Cuáles de las siguientes son funciones de agregación?', 'Hay dos respuestas correctas', 'OPCION_MULTIPLE', '2026-10-03 11:15', NULL, 3),
    -- Factor 4: Modelado de datos
    (14, 1, 'Tercera forma normal', '¿Qué busca eliminar la tercera forma normal?', NULL, 'OPCION_SIMPLE', '2026-10-03 11:20', NULL, 4),
    (15, 1, 'Relación muchos a muchos', '¿Cómo se implementa una relación muchos a muchos en un modelo relacional?', NULL, 'OPCION_SIMPLE', '2026-10-03 11:25', NULL, 4),
    -- Factor 5: Colaboración
    (16, 1, 'Compartir información', '¿Con qué frecuencia compartís con tu equipo información que te resultó útil?', 'Escala de frecuencia', 'OPCION_SIMPLE', '2026-10-04 12:00', NULL, 5),
    (17, 1, 'Pedir ayuda', '¿Con qué frecuencia pedís ayuda cuando una tarea te bloquea?', 'Escala de frecuencia', 'OPCION_SIMPLE', '2026-10-04 12:05', NULL, 5),
    (18, 1, 'Compañero atrasado', 'Un compañero no llega con su parte de la entrega. ¿Qué hacés?', NULL, 'OPCION_SIMPLE', '2026-10-04 12:10', NULL, 5),
    -- Factor 6: Resolución de conflictos
    (19, 1, 'Desacuerdo técnico', 'En una reunión no estás de acuerdo con la solución que propone el equipo. ¿Qué hacés?', NULL, 'OPCION_SIMPLE', '2026-10-04 12:15', NULL, 6),
    (20, 1, 'Recibir críticas', '¿Con qué frecuencia aceptás críticas sobre tu trabajo sin ponerte a la defensiva?', 'Escala de frecuencia', 'OPCION_SIMPLE', '2026-10-04 12:20', NULL, 6),
    -- Factor 7: Comunicación oral
    (21, 1, 'Presentaciones', '¿Cómo evaluarías tu desempeño al presentar ideas frente a un grupo?', 'Escala de valoración', 'OPCION_SIMPLE', '2026-10-05 08:30', NULL, 7),
    (22, 1, 'Escucha activa', '¿Qué caracteriza a la escucha activa?', NULL, 'OPCION_SIMPLE', '2026-10-05 08:35', NULL, 7),
    -- Factor 8: Comunicación escrita (la 24 fue modificada: version 2)
    (23, 1, 'Correos formales', '¿Cómo evaluarías tu redacción de correos formales?', 'Escala de valoración', 'OPCION_SIMPLE', '2026-10-05 08:40', NULL, 8),
    (24, 2, 'Informes técnicos', 'Un buen informe técnico empieza por las conclusiones principales.', 'Verdadero o falso', 'OPCION_SIMPLE', '2026-11-02 10:15', NULL, 8),
    (25, 1, 'Revisión de ortografía', '¿Con qué frecuencia revisás la ortografía antes de enviar un texto?', 'Escala de frecuencia', 'OPCION_SIMPLE', '2026-10-05 08:50', NULL, 8),
    -- Factor 9: Toma de decisiones (1 activa + 1 dada de baja -> no alcanza)
    (26, 1, 'Decisiones bajo presión', 'Tenés que tomar una decisión urgente sin contar con toda la información. ¿Qué hacés?', NULL, 'OPCION_SIMPLE', '2026-10-06 14:00', NULL, 9),
    (27, 1, 'Buscar consenso', '¿Con qué frecuencia buscás el consenso del equipo antes de decidir?', 'Escala de frecuencia', 'OPCION_SIMPLE', '2026-10-06 14:05', '2026-11-05 12:00', 9),
    -- Factor 10: Motivación del equipo (1 sola pregunta -> no alcanza)
    (28, 1, 'Reconocer el trabajo', '¿Con qué frecuencia reconocés públicamente el trabajo de tu equipo?', 'Escala de frecuencia', 'OPCION_SIMPLE', '2026-10-06 14:10', NULL, 10),
    -- Factor 12: Pruebas unitarias (el factor esta dado de baja)
    (29, 1, 'Uso de mocks', '¿Para qué se usa un mock en una prueba unitaria?', NULL, 'OPCION_SIMPLE', '2026-09-20 16:00', NULL, 12),
    (30, 1, 'Cobertura de código', 'Tener 100% de cobertura garantiza que el código no tiene errores.', 'Verdadero o falso', 'OPCION_SIMPLE', '2026-09-20 16:05', NULL, 12),
    -- Factor 13: Sintaxis COBOL (la competencia esta dada de baja)
    (31, 1, 'Divisiones COBOL', '¿Cuál es la primera división de un programa COBOL?', NULL, 'OPCION_SIMPLE', '2026-09-01 10:00', NULL, 13),
    (32, 1, 'Cláusula PIC', 'La cláusula PIC define el formato de un campo.', 'Verdadero o falso', 'OPCION_SIMPLE', '2026-09-01 10:05', NULL, 13);

-- =====================================================================
-- Opciones (el id lo genera la base)
-- Escalas usadas:
--   frecuencia: Nunca 0, Pocas veces 1, Casi siempre 3, Siempre 6
--   valoracion: Malo 0, Regular 1, Bueno 2, Muy bueno 3, Excelente 4
-- =====================================================================
INSERT INTO opcion (pregunta_id, orden_visualizacion, texto, ponderacion) VALUES
    (1, 1, 'Herencia', 0),
    (1, 2, 'Encapsulamiento', 10),
    (1, 3, 'Polimorfismo', 0),
    (1, 4, 'Abstracción', 0),

    (2, 1, 'Verdadero', 0),
    (2, 2, 'Falso', 10),

    (3, 1, 'Una clase puede implementar varias interfaces', 5),
    (3, 2, 'Pueden tener métodos default con implementación', 5),
    (3, 3, 'Pueden tener constructores', 0),
    (3, 4, 'Se pueden instanciar con new', 0),

    (4, 1, 'Tratar objetos de distintas clases a través de un mismo tipo', 10),
    (4, 2, 'Crear varias instancias de una misma clase', 0),
    (4, 3, 'Ocultar los atributos de una clase', 0),

    (5, 1, 'El tipo de retorno', 0),
    (5, 2, 'La lista de parámetros', 10),
    (5, 3, 'El modificador de acceso', 0),

    (6, 1, 'Verdadero', 0),
    (6, 2, 'Falso', 10),

    (33, 1, 'Verdadero', 0),
    (33, 2, 'Falso', 10),

    (7, 1, 'IOException', 10),
    (7, 2, 'NullPointerException', 0),
    (7, 3, 'IllegalArgumentException', 0),

    (8, 1, 'Verdadero', 10),
    (8, 2, 'Falso', 0),

    (9, 1, 'Cierra los recursos automáticamente al terminar', 5),
    (9, 2, 'El recurso tiene que implementar AutoCloseable', 5),
    (9, 3, 'No permite usar bloques catch', 0),
    (9, 4, 'Solo funciona con archivos', 0),

    (10, 1, 'Solo las filas que tienen coincidencia en ambas tablas', 10),
    (10, 2, 'Todas las filas de la tabla de la izquierda', 0),
    (10, 3, 'El producto cartesiano de las dos tablas', 0),

    (11, 1, 'Filtrar grupos después de aplicar funciones de agregación', 10),
    (11, 2, 'Filtrar filas antes de agruparlas', 0),
    (11, 3, 'Ordenar el resultado', 0),

    (12, 1, 'Verdadero', 10),
    (12, 2, 'Falso', 0),

    (13, 1, 'COUNT', 5),
    (13, 2, 'SUM', 5),
    (13, 3, 'UPPER', 0),
    (13, 4, 'COALESCE', 0),

    (14, 1, 'Las dependencias transitivas', 10),
    (14, 2, 'Los grupos repetitivos', 0),
    (14, 3, 'Las claves primarias compuestas', 0),

    (15, 1, 'Con una tabla intermedia que tiene una FK a cada tabla', 10),
    (15, 2, 'Con una FK en cada una de las dos tablas', 0),
    (15, 3, 'Con un atributo multivaluado', 0),

    (16, 1, 'Nunca', 0),
    (16, 2, 'Pocas veces', 1),
    (16, 3, 'Casi siempre', 3),
    (16, 4, 'Siempre', 6),

    (17, 1, 'Nunca', 0),
    (17, 2, 'Pocas veces', 1),
    (17, 3, 'Casi siempre', 3),
    (17, 4, 'Siempre', 6),

    (18, 1, 'Le ofrezco ayuda y aviso al equipo', 7),
    (18, 2, 'Hago su parte sin decir nada', 2),
    (18, 3, 'Espero a que lo resuelva solo', 1),
    (18, 4, 'Lo reporto sin hablar antes con él', 0),

    (19, 1, 'Explico mi postura con argumentos y escucho la de los demás', 8),
    (19, 2, 'Acepto la propuesta sin opinar', 1),
    (19, 3, 'Insisto hasta que cambien de idea', 1),
    (19, 4, 'Me desentiendo del tema', 0),

    (20, 1, 'Nunca', 0),
    (20, 2, 'Pocas veces', 1),
    (20, 3, 'Casi siempre', 3),
    (20, 4, 'Siempre', 6),

    (21, 1, 'Malo', 0),
    (21, 2, 'Regular', 1),
    (21, 3, 'Bueno', 2),
    (21, 4, 'Muy bueno', 3),
    (21, 5, 'Excelente', 4),

    (22, 1, 'Prestar atención y repreguntar para confirmar lo entendido', 10),
    (22, 2, 'Preparar la respuesta mientras el otro habla', 0),
    (22, 3, 'Interrumpir para aclarar cada punto', 0),

    (23, 1, 'Malo', 0),
    (23, 2, 'Regular', 1),
    (23, 3, 'Bueno', 2),
    (23, 4, 'Muy bueno', 3),
    (23, 5, 'Excelente', 4),

    (24, 1, 'Verdadero', 10),
    (24, 2, 'Falso', 0),

    (25, 1, 'Nunca', 0),
    (25, 2, 'Pocas veces', 1),
    (25, 3, 'Casi siempre', 3),
    (25, 4, 'Siempre', 6),

    (26, 1, 'Decido con lo que tengo y dejo claro el riesgo', 7),
    (26, 2, 'Espero a tener toda la información', 2),
    (26, 3, 'Le paso la decisión a otra persona', 1),

    (27, 1, 'Nunca', 0),
    (27, 2, 'Pocas veces', 1),
    (27, 3, 'Casi siempre', 3),
    (27, 4, 'Siempre', 6),

    (28, 1, 'Nunca', 0),
    (28, 2, 'Pocas veces', 1),
    (28, 3, 'Casi siempre', 3),
    (28, 4, 'Siempre', 6),

    (29, 1, 'Reemplazar una dependencia por un objeto controlado', 10),
    (29, 2, 'Medir la cobertura de las pruebas', 0),
    (29, 3, 'Ejecutar las pruebas en paralelo', 0),

    (30, 1, 'Verdadero', 0),
    (30, 2, 'Falso', 10),

    (31, 1, 'IDENTIFICATION DIVISION', 10),
    (31, 2, 'DATA DIVISION', 0),
    (31, 3, 'PROCEDURE DIVISION', 0),

    (32, 1, 'Verdadero', 10),
    (32, 2, 'Falso', 0);

-- =====================================================================
-- Auditoria de las preguntas dadas de baja (CU023)
-- =====================================================================
INSERT INTO registro_auditoria (id, fecha_eliminacion, hora_eliminacion, usuario_responsable, pregunta_id) VALUES
    (1, '2026-10-20', '14:32:00', 'consultor.garcia', 33),
    (2, '2026-11-05', '09:10:00', 'consultor.ruiz', 27);

-- =====================================================================
-- Puestos y competencias requeridas (ponderacion 0 a 10)
-- =====================================================================
INSERT INTO puesto (id, codigo, nombre, descripcion, empresa_id) VALUES
    (1, 'DEV01', 'Desarrollador Java', 'Desarrollo de aplicaciones backend con Java y Spring', 1),
    (2, 'ANA01', 'Analista funcional', 'Relevamiento de requerimientos y documentación', 2),
    (3, 'LID01', 'Líder de proyecto', 'Coordinación de equipos de desarrollo', 1),
    (4, 'QA01',  'Tester QA', 'Diseño y ejecución de casos de prueba', 3),
    (5, 'SOP01', 'Soporte técnico', 'Atención de incidentes de infraestructura', 3),
    (6, 'DBA01', 'Administrador de bases de datos', 'Mantenimiento y optimización de bases de datos', 2);

INSERT INTO caracteristica_puesto (id, ponderacion, puesto_id, competencia_id) VALUES
    -- DEV01: todas evaluables
    (1,  8, 1, 1),
    (2,  6, 1, 2),
    (3,  5, 1, 3),
    -- ANA01: todas evaluables
    (4,  6, 2, 3),
    (5,  7, 2, 4),
    -- LID01: Liderazgo no es evaluable
    (6,  7, 3, 3),
    (7,  6, 3, 4),
    (8,  8, 3, 5),
    -- QA01: Testing no es evaluable
    (9,  5, 4, 1),
    (10, 8, 4, 7),
    -- SOP01: Redes no es evaluable
    (11, 6, 5, 6),
    (12, 5, 5, 4),
    -- DBA01: todas evaluables
    (13, 9, 6, 2),
    (14, 4, 6, 3);

-- =====================================================================
-- Candidatos
-- Hay apellidos con tilde para probar la busqueda sin acentos (unaccent)
-- y dos Gomez para probar resultados multiples.
-- =====================================================================
INSERT INTO candidato (id, nro_candidato, apellido, nombre, tipo_doc, nro_doc, fecha_nacimiento, genero, nacionalidad, email, escolaridad, eliminado) VALUES
    (1,  1001, 'Gómez',     'María',      'DNI', 38111222, '1994-03-12', 'M', 'Argentina', 'maria.gomez@mail.com',      'UNIVERSITARIO_COMPLETO',   FALSE),
    (2,  1002, 'Gómez',     'Juan Pablo', 'DNI', 36222333, '1991-07-25', 'H', 'Argentina', 'juanpablo.gomez@mail.com',  'UNIVERSITARIO_COMPLETO',   FALSE),
    (3,  1003, 'Pérez',     'Lucía',      'DNI', 40333444, '1997-11-02', 'M', 'Argentina', 'lucia.perez@mail.com',      'UNIVERSITARIO_INCOMPLETO', FALSE),
    (4,  1004, 'Martínez',  'Santiago',   'DNI', 35444555, '1990-01-30', 'H', 'Argentina', 'santiago.martinez@mail.com','UNIVERSITARIO_COMPLETO',   FALSE),
    (5,  1005, 'Núñez',     'Camila',     'DNI', 41555666, '1998-05-18', 'M', 'Argentina', 'camila.nunez@mail.com',     'SECUNDARIO_COMPLETO',      FALSE),
    (6,  1006, 'Fernández', 'Tomás',      'DNI', 39666777, '1996-09-09', 'H', 'Argentina', 'tomas.fernandez@mail.com',  'UNIVERSITARIO_INCOMPLETO', FALSE),
    (7,  1007, 'Rodríguez', 'Valentina',  'DNI', 37777888, '1993-12-21', 'M', 'Argentina', 'valentina.rodriguez@mail.com', 'UNIVERSITARIO_COMPLETO', FALSE),
    (8,  1008, 'López',     'Martín',     'DNI', 34888999, '1989-04-14', 'H', 'Argentina', 'martin.lopez@mail.com',     'UNIVERSITARIO_COMPLETO',   FALSE),
    (9,  1009, 'Silva',     'Ana',        'PP',   7812345, '1995-06-03', 'M', 'Uruguaya',  'ana.silva@mail.com',        'UNIVERSITARIO_COMPLETO',   FALSE),
    (10, 1010, 'Díaz',      'Federico',   'LE',  12345678, '1965-06-03', 'H', 'Argentina', 'federico.diaz@mail.com',    'SECUNDARIO_COMPLETO',      FALSE),
    (11, 1011, 'Romero',    'Julieta',    'LC',  13456789, '1968-08-27', 'M', 'Argentina', 'julieta.romero@mail.com',   'PRIMARIO_COMPLETO',        FALSE),
    (12, 1012, 'Acosta',    'Bruno',      'DNI', 42999000, '2000-02-11', 'H', 'Argentina', 'bruno.acosta@mail.com',     'SECUNDARIO_COMPLETO',      FALSE),
    (13, 1013, 'Benítez',   'Sofía',      'DNI', 40123123, '1997-10-06', 'M', 'Argentina', 'sofia.benitez@mail.com',    'UNIVERSITARIO_INCOMPLETO', FALSE),
    (14, 1014, 'Castro',    'Nicolás',    'DNI', 38456456, '1995-03-29', 'H', 'Paraguaya', 'nicolas.castro@mail.com',   'UNIVERSITARIO_COMPLETO',   FALSE),
    (15, 1015, 'Herrera',   'Paula',      'DNI', 36789789, '1992-07-15', 'M', 'Argentina', 'paula.herrera@mail.com',    'UNIVERSITARIO_COMPLETO',   TRUE),
    (16, 1016, 'Sosa',      'Diego',      'DNI', 41010101, '1999-01-08', 'H', 'Argentina', 'diego.sosa@mail.com',       'SECUNDARIO_COMPLETO',      FALSE);

-- =====================================================================
-- Evaluaciones
-- La 3 se crea con la fecha del dia en que se corre el seed,
-- asi sus cuestionarios ACTIVO / EN_PROCESO siguen dentro de plazo.
-- =====================================================================
INSERT INTO evaluacion (id, codigo, fecha_creacion, fecha_cierre, duracion_en_minutos, cant_preguntas_por_bloque, puesto_id) VALUES
    (1, 'EV-DEV01-20261103-0930', '2026-11-03 09:30', '2026-11-10', 60, 5, 1),
    (2, 'EV-ANA01-20261110-1100', '2026-11-10 11:00', '2026-11-17', 60, 5, 2),
    (3, 'EV-DBA01-' || to_char(LOCALTIMESTAMP, 'YYYYMMDD-HH24MI'), LOCALTIMESTAMP, NULL, 60, 5, 6);

-- =====================================================================
-- Cuestionarios
-- Claves para el login de candidato (CU001 -> CU026):
--   Silva Ana         PP  7812345   clave Hx7kPq2m  (ACTIVO, todavia no empezo)
--   Diaz Federico     LE  12345678  clave Tz4wRb9n  (ACTIVO, todavia no empezo)
--   Acosta Bruno      DNI 42999000  clave Mv3sJd8e  (EN_PROCESO, empezo hace 10 minutos)
-- El puntaje va de 0 a 10 (promedio de las competencias).
-- =====================================================================
INSERT INTO cuestionario (id, clave_ingreso, fecha_hora_inicio, fecha_hora_fin, ultimo_ingreso, cantidad_accesos, puntaje_total, estado, evaluacion_id, candidato_id) VALUES
    -- Evaluacion 1 (DEV01), cerrada
    (1,  'Ab3dEf5g', '2026-11-04 10:00', '2026-11-04 10:42', '2026-11-04 10:00', 1, 8.45, 'COMPLETADO',    1, 1),
    (2,  'Qw2eRt6y', '2026-11-04 15:00', '2026-11-04 15:51', '2026-11-04 15:00', 1, 6.30, 'COMPLETADO',    1, 3),
    (3,  'Zx5cVb7n', '2026-11-05 09:30', '2026-11-05 10:05', '2026-11-05 09:48', 2, 7.10, 'COMPLETADO',    1, 4),
    (4,  'Kj8hGf4d', '2026-11-05 15:00', '2026-11-05 16:00', '2026-11-05 15:20', 2, NULL, 'INCOMPLETO',    1, 6),
    (5,  'Np6mWq3s', NULL,               '2026-11-10 23:59', NULL,               0, NULL, 'SIN_CONTESTAR', 1, 8),
    -- Evaluacion 2 (ANA01), cerrada
    (6,  'Ys9tUv2w', '2026-11-11 11:00', '2026-11-11 11:38', '2026-11-11 11:00', 1, 9.05, 'COMPLETADO',    2, 7),
    (7,  'Fg4hJk7m', '2026-11-12 18:00', '2026-11-12 18:55', '2026-11-12 18:00', 1, 5.40, 'COMPLETADO',    2, 5),
    (8,  'Rt5yUp8q', '2026-11-13 08:15', '2026-11-13 08:59', '2026-11-13 08:15', 1, 7.80, 'COMPLETADO',    2, 2),
    -- Evaluacion 3 (DBA01), en curso
    (9,  'Hx7kPq2m', NULL, NULL, NULL, 0, NULL, 'ACTIVO', 3, 9),
    (10, 'Tz4wRb9n', NULL, NULL, NULL, 0, NULL, 'ACTIVO', 3, 10),
    (11, 'Mv3sJd8e', LOCALTIMESTAMP - INTERVAL '10 minutes', NULL, LOCALTIMESTAMP - INTERVAL '10 minutes', 1, NULL, 'EN_PROCESO', 3, 12);

-- =====================================================================
-- Ajuste de secuencias: como los ids se cargaron a mano, la proxima
-- alta desde la API tiene que seguir despues del ultimo id.
-- =====================================================================
SELECT setval(pg_get_serial_sequence('empresa', 'id'),               (SELECT MAX(id) FROM empresa));
SELECT setval(pg_get_serial_sequence('competencia', 'id'),           (SELECT MAX(id) FROM competencia));
SELECT setval(pg_get_serial_sequence('factor', 'id'),                (SELECT MAX(id) FROM factor));
SELECT setval(pg_get_serial_sequence('pregunta', 'id'),              (SELECT MAX(id) FROM pregunta));
SELECT setval(pg_get_serial_sequence('registro_auditoria', 'id'),    (SELECT MAX(id) FROM registro_auditoria));
SELECT setval(pg_get_serial_sequence('puesto', 'id'),                (SELECT MAX(id) FROM puesto));
SELECT setval(pg_get_serial_sequence('caracteristica_puesto', 'id'), (SELECT MAX(id) FROM caracteristica_puesto));
SELECT setval(pg_get_serial_sequence('candidato', 'id'),             (SELECT MAX(id) FROM candidato));
SELECT setval(pg_get_serial_sequence('evaluacion', 'id'),            (SELECT MAX(id) FROM evaluacion));
SELECT setval(pg_get_serial_sequence('cuestionario', 'id'),          (SELECT MAX(id) FROM cuestionario));

COMMIT;
