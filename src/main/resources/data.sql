-- Inserta el usuario de prueba solo si no existe previamente
INSERT INTO Usuario (id, email, password, rol, activo)
SELECT null, 'test@unlam.edu.ar', 'test', 'ADMIN', true
    WHERE NOT EXISTS (
    SELECT 1 FROM Usuario WHERE email = 'test@unlam.edu.ar'
);

INSERT INTO Cancha(id, nombre, localidad, latitud, longitud)
VALUES(null, 'Cancha Los Leones', 'San Justo', -34.6798, -58.5608);

INSERT INTO Cancha(id, nombre, localidad, latitud, longitud)
VALUES(null, 'Cancha 5', 'San Justo', -34.6810, -58.5580);

INSERT INTO Cancha(id, nombre, localidad, latitud, longitud)
VALUES(null, 'Cancha El Potrero', 'Laferrere', -34.7480, -58.5840);

INSERT INTO Cancha(id, nombre, localidad, latitud, longitud)
VALUES(null, 'Cancha Los Pibes', 'Laferrere', -34.7500, -58.5810);

INSERT INTO Cancha(id, nombre, localidad, latitud, longitud)
VALUES(null, 'Cancha La Quinta', 'Ramos Mejia', -34.6440, -58.5630);

INSERT INTO Cancha(id, nombre, localidad, latitud, longitud)
VALUES(null, 'Cancha El Fortin', 'Ramos Mejia', -34.6460, -58.5600);

INSERT INTO Jugador(nombre, posicion1, posicion2, puntos, nivel)
VALUES('Alan', 'Delantero', 'Arquero', 40, "B");

INSERT INTO Jugador(nombre, posicion1, posicion2,  puntos, nivel)
VALUES('Axel', 'Delantero', 'Arquero', 50, "A");

INSERT INTO Jugador(nombre, posicion1, posicion2,  puntos, nivel)
VALUES('Santi', 'Defensor', 'Mediocampista', 70, "A");

INSERT INTO Jugador(nombre, posicion1, posicion2, puntos, nivel)
VALUES('JuanPa', 'Arquero', 'Defensor', 20, "C");

INSERT INTO Jugador(nombre, posicion1, posicion2,  puntos, nivel)
VALUES('Tomas', 'Defensor', 'Arquero', 0, "D");


-- Crear un jugador exclusivo para el usuario de prueba
INSERT INTO Jugador (
    nombre,
    posicion1,
    posicion2,
    puntos,
    nivel,
    usuario_id,
    partidosJugados,
    partidosGanados,
    partidosPerdidos
)
VALUES (
           'Jugador Test',
           'Delantero',
           'Mediocampista',
           60,
           'A',
           (SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar' LIMIT 1),
    3,
    2,
    1
    );

-- Cargar estadísticas de ejemplo para ese jugador
INSERT INTO Estadistica (
    goles,
    asistencias,
    amarillas,
    rojas,
    puntaje,
    jugador_id,
    partido_id
)
VALUES (
           3,
           2,
           1,
           0,
           8,
           (
               SELECT id
               FROM Jugador
               WHERE usuario_id = (
                   SELECT id
                   FROM Usuario
                   WHERE email = 'test@unlam.edu.ar' LIMIT 1
       )
    LIMIT 1
           ),
           NULL
       );

-- ============================================================
-- HISTORIAL DEL USUARIO DE PRUEBA
-- Un único partido finalizado con estadísticas individuales.
-- ============================================================

SET @usuario_test = (
    SELECT id
    FROM Usuario
    WHERE email = 'test@unlam.edu.ar'
    LIMIT 1
);

SET @jugador_test = (
    SELECT id
    FROM Jugador
    WHERE usuario_id = @usuario_test
    LIMIT 1
);

-- Crear los equipos
INSERT INTO equipo (nombre)
VALUES ('Los Testers');

SET @equipo_testers = LAST_INSERT_ID();

INSERT INTO equipo (nombre)
VALUES ('Los Devs');

SET @equipo_devs = LAST_INSERT_ID();


-- Crear un único partido finalizado
INSERT INTO Partido (
    nivel,
    esPrivado,
    fecha,
    cancha_id,
    cupoMaximo,
    codigoDeAcceso,
    distancia,
    cantJugadores,
    estado,
    estadoEstadisticas,
    organizador_id,
    equipo1_id,
    equipo2_id
)
VALUES (
           'A',
           false,
           '2026-10-30 20:00:00',
           (
               SELECT id
               FROM Cancha
               WHERE nombre = 'Cancha Los Leones'
               ORDER BY id DESC
               LIMIT 1
       ),
    6,
    NULL,
    0,
    6,
    'FINALIZADO',
    'CONFIRMADA',
    @usuario_test,
    @equipo_testers,
    @equipo_devs
    );

SET @partido_test = LAST_INSERT_ID();


-- Inscribir a Jugador Test
INSERT INTO InscripcionPartido (
    partido_id,
    jugador_id,
    equipo_id,
    confirmado,
    esCapitan,
    fechaInscripcion
)
VALUES (
           @partido_test,
           @jugador_test,
           @equipo_testers,
           true,
           true,
           '2026-10-29 18:00:00'
       );


-- Registrar las estadísticas individuales del jugador
INSERT INTO Estadistica (
    goles,
    asistencias,
    amarillas,
    rojas,
    puntaje,
    jugador_id,
    partido_id
)
VALUES (
           1,
           1,
           0,
           0,
           9,
           @jugador_test,
           @partido_test
       );