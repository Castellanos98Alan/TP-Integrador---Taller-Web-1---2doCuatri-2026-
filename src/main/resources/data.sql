-- ArmoLaCancha: datos iniciales de desarrollo.
-- Unico usuario/jugador: test@unlam.edu.ar / Jugador Test.
-- Script reejecutable con hibernate.hbm2ddl.auto=update.
-- No crea Alan, Axel, Santi, JuanPa, Tomas ni Castel.

INSERT INTO Usuario (email, password, rol, activo)
SELECT 'test@unlam.edu.ar', 'test', 'ADMIN', true
    WHERE NOT EXISTS (
    SELECT 1 FROM Usuario WHERE email = 'test@unlam.edu.ar'
);

-- Canchas de ejemplo (no son usuarios).
INSERT INTO Cancha (nombre, localidad, latitud, longitud, imagen_url)
SELECT 'Cancha Los Leones', 'San Justo', -34.6798, -58.5608, 'uploads/canchas/cancha1.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM Cancha WHERE nombre = 'Cancha Los Leones' AND localidad = 'San Justo');

INSERT INTO Cancha (nombre, localidad, latitud, longitud, imagen_url)
SELECT 'Cancha 5', 'San Justo', -34.6810, -58.5580, 'uploads/canchas/cancha2.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM Cancha WHERE nombre = 'Cancha 5' AND localidad = 'San Justo');

INSERT INTO Cancha (nombre, localidad, latitud, longitud, imagen_url)
SELECT 'Cancha El Potrero', 'Laferrere', -34.7480, -58.5840, 'uploads/canchas/cancha3.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM Cancha WHERE nombre = 'Cancha El Potrero' AND localidad = 'Laferrere');

INSERT INTO Cancha (nombre, localidad, latitud, longitud, imagen_url)
SELECT 'Cancha Los Pibes', 'Laferrere', -34.7500, -58.5810, 'uploads/canchas/cancha4.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM Cancha WHERE nombre = 'Cancha Los Pibes' AND localidad = 'Laferrere');

INSERT INTO Cancha (nombre, localidad, latitud, longitud, imagen_url)
SELECT 'Cancha La Quinta', 'Ramos Mejia', -34.6440, -58.5630, 'uploads/canchas/cancha5.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM Cancha WHERE nombre = 'Cancha La Quinta' AND localidad = 'Ramos Mejia');

INSERT INTO Cancha (nombre, localidad, latitud, longitud, imagen_url)
SELECT 'Cancha El Fortin', 'Ramos Mejia', -34.6460, -58.5600, 'uploads/canchas/cancha6.jpg'
    WHERE NOT EXISTS (SELECT 1 FROM Cancha WHERE nombre = 'Cancha El Fortin' AND localidad = 'Ramos Mejia');

-- Perfil asociado al usuario Test.
INSERT INTO Jugador (nombre, posicion1, posicion2, puntos, nivel, usuario_id,
                     partidosJugados, partidosGanados, partidosPerdidos)
SELECT 'Jugador Test', 'Delantero', 'Mediocampista', 81, 'S', u.id, 3, 1, 1
FROM Usuario u
WHERE u.email = 'test@unlam.edu.ar'
  AND NOT EXISTS (SELECT 1 FROM Jugador j WHERE j.usuario_id = u.id);

-- Valores iniciales del jugador de prueba.
-- Los 81 puntos quedan precargados solo para este escenario de desarrollo.
UPDATE Jugador
SET puntos = 81,
    nivel = 'S',
    partidosJugados = 3,
    partidosGanados = 1,
    partidosPerdidos = 1
WHERE usuario_id = (SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar');

-- Equipos del unico partido historico de ejemplo.
INSERT INTO equipo (nombre)
SELECT 'Los Testers'
    WHERE NOT EXISTS (SELECT 1 FROM equipo WHERE nombre = 'Los Testers');

INSERT INTO equipo (nombre)
SELECT 'Los Devs'
    WHERE NOT EXISTS (SELECT 1 FROM equipo WHERE nombre = 'Los Devs');

-- Un unico partido finalizado organizado por el usuario Test.
INSERT INTO Partido (
    nivel, esPrivado, fecha, cancha_id, cupoMaximo, codigoDeAcceso,
    distancia, cantJugadores, estado, estadoEstadisticas,
    organizador_id, equipo1_id, equipo2_id
)
SELECT
    'A', false, '2026-10-30 20:00:00', c.id, 6, NULL,
    0, 6, 'FINALIZADO', 'CONFIRMADA', u.id, e1.id, e2.id
FROM Usuario u
         JOIN Cancha c ON c.nombre = 'Cancha Los Leones' AND c.localidad = 'San Justo'
         JOIN equipo e1 ON e1.nombre = 'Los Testers'
         JOIN equipo e2 ON e2.nombre = 'Los Devs'
WHERE u.email = 'test@unlam.edu.ar'
  AND NOT EXISTS (
    SELECT 1 FROM Partido p
    WHERE p.organizador_id = u.id AND p.fecha = '2026-10-30 20:00:00'
);

-- Segundo partido finalizado de octubre.
INSERT INTO Partido (
    nivel, esPrivado, fecha, cancha_id, cupoMaximo, codigoDeAcceso,
    distancia, cantJugadores, estado, estadoEstadisticas,
    organizador_id, equipo1_id, equipo2_id
)
SELECT
    'A', false, '2026-10-23 20:00:00', c.id, 6, NULL,
    0, 6, 'FINALIZADO', 'CONFIRMADA', u.id, e1.id, e2.id
FROM Usuario u
         JOIN Cancha c ON c.nombre = 'Cancha Los Leones' AND c.localidad = 'San Justo'
         JOIN equipo e1 ON e1.nombre = 'Los Testers'
         JOIN equipo e2 ON e2.nombre = 'Los Devs'
WHERE u.email = 'test@unlam.edu.ar'
  AND NOT EXISTS (
    SELECT 1 FROM Partido p
    WHERE p.organizador_id = u.id AND p.fecha = '2026-10-23 20:00:00'
);

-- Inscripcion del Jugador Test en ese partido.

SET @jugador_test = (
    SELECT id FROM Jugador
    WHERE usuario_id = (
        SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'
    )
);

SET @equipo_testers = (
    SELECT id FROM equipo WHERE nombre = 'Los Testers'
);

-- Eliminar estadisticas historicas precargadas que no pertenecen a un partido.
-- Las estadisticas del perfil se obtienen exclusivamente de partidos.
DELETE FROM Estadistica
WHERE jugador_id = @jugador_test
  AND partido_id IS NULL;

SET @partido_test = (
    SELECT id FROM Partido
    WHERE fecha = '2026-10-30 20:00:00'
      AND organizador_id = (
          SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'
      )
);

SET @partido_test_2 = (
    SELECT id FROM Partido
    WHERE fecha = '2026-10-23 20:00:00'
      AND organizador_id = (
          SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'
      )
);

INSERT INTO InscripcionPartido (
    partido_id,
    jugador_id,
    equipo_id,
    confirmado,
    esCapitan,
    fechaInscripcion,
    recordatorioEnviado
)
SELECT
    p.id,
    j.id,
    e.id,
    true,
    true,
    '2026-10-29 18:00:00',
    false
FROM Partido p
         JOIN Usuario u ON p.organizador_id = u.id
         JOIN Jugador j ON j.usuario_id = u.id
         JOIN equipo e ON e.nombre = 'Los Testers'
WHERE u.email = 'test@unlam.edu.ar'
  AND p.fecha = '2026-10-30 20:00:00'
  AND NOT EXISTS (
    SELECT 1 FROM InscripcionPartido ip
    WHERE ip.partido_id = p.id
      AND ip.jugador_id = j.id
);

INSERT INTO InscripcionPartido (
    partido_id,
    jugador_id,
    equipo_id,
    confirmado,
    esCapitan,
    fechaInscripcion,
    recordatorioEnviado
)
SELECT
    p.id,
    j.id,
    e.id,
    true,
    true,
    '2026-10-22 18:00:00',
    false
FROM Partido p
         JOIN Usuario u ON p.organizador_id = u.id
         JOIN Jugador j ON j.usuario_id = u.id
         JOIN equipo e ON e.nombre = 'Los Testers'
WHERE u.email = 'test@unlam.edu.ar'
  AND p.fecha = '2026-10-23 20:00:00'
  AND NOT EXISTS (
    SELECT 1 FROM InscripcionPartido ip
    WHERE ip.partido_id = p.id
      AND ip.jugador_id = j.id
);


-- Estadistica individual para el historial del partido, sin duplicarla.
INSERT INTO Estadistica (
    goles, asistencias, amarillas, rojas, puntaje, jugador_id, partido_id
)
SELECT 1, 1, 0, 0, 9, j.id, p.id
FROM Partido p
         JOIN Usuario u ON p.organizador_id = u.id
         JOIN Jugador j ON j.usuario_id = u.id
WHERE u.email = 'test@unlam.edu.ar'
  AND p.fecha = '2026-10-30 20:00:00'
  AND NOT EXISTS (
    SELECT 1 FROM Estadistica es
    WHERE es.jugador_id = j.id AND es.partido_id = p.id
);
-- Mantener los datos del primer partido aunque la estadistica ya existiera.
UPDATE Estadistica es
JOIN Partido p ON p.id = es.partido_id
SET es.goles = 1, es.asistencias = 1, es.amarillas = 0, es.rojas = 0, es.puntaje = 9
WHERE es.jugador_id = @jugador_test
  AND p.fecha = '2026-10-30 20:00:00';

INSERT INTO Estadistica (
    goles, asistencias, amarillas, rojas, puntaje, jugador_id, partido_id
)
SELECT 0, 1, 0, 0, 7, j.id, p.id
FROM Partido p
         JOIN Usuario u ON p.organizador_id = u.id
         JOIN Jugador j ON j.usuario_id = u.id
WHERE u.email = 'test@unlam.edu.ar'
  AND p.fecha = '2026-10-23 20:00:00'
  AND NOT EXISTS (
    SELECT 1 FROM Estadistica es
    WHERE es.jugador_id = j.id AND es.partido_id = p.id
);
-- Mantener los datos del segundo partido aunque la estadistica ya existiera.
UPDATE Estadistica es
JOIN Partido p ON p.id = es.partido_id
SET es.goles = 0, es.asistencias = 1, es.amarillas = 0, es.rojas = 0, es.puntaje = 7
WHERE es.jugador_id = @jugador_test
  AND p.fecha = '2026-10-23 20:00:00';

-- Resultado del primer partido: victoria de Los Testers (2 - 1).
SET @resultado_test = (
    SELECT resultado_id FROM Partido
    WHERE id = @partido_test
);

INSERT INTO Resultado (golesLocal, golesVisitante, fechaRegistro)
SELECT 2, 1, NOW()
    WHERE @resultado_test IS NULL;

SET @resultado_test = IFNULL(@resultado_test, LAST_INSERT_ID());

UPDATE Partido
SET resultado_id = @resultado_test
WHERE id = @partido_test
  AND resultado_id IS NULL;

-- Resultado del segundo partido: derrota de Los Testers (1 - 3).
SET @resultado_test_2 = (
    SELECT resultado_id FROM Partido
    WHERE id = @partido_test_2
);

INSERT INTO Resultado (golesLocal, golesVisitante, fechaRegistro)
SELECT 1, 3, NOW()
    WHERE @resultado_test_2 IS NULL;

SET @resultado_test_2 = IFNULL(@resultado_test_2, LAST_INSERT_ID());

UPDATE Partido
SET resultado_id = @resultado_test_2
WHERE id = @partido_test_2
  AND resultado_id IS NULL;

-- Tercer partido finalizado de noviembre: empate.
INSERT INTO Partido (
    nivel, esPrivado, fecha, cancha_id, cupoMaximo, codigoDeAcceso,
    distancia, cantJugadores, estado, estadoEstadisticas,
    organizador_id, equipo1_id, equipo2_id
)
SELECT
    'A', false, '2026-11-06 20:00:00', c.id, 6, NULL,
    0, 6, 'FINALIZADO', 'CONFIRMADA', u.id, e1.id, e2.id
FROM Usuario u
         JOIN Cancha c ON c.nombre = 'Cancha Los Leones' AND c.localidad = 'San Justo'
         JOIN equipo e1 ON e1.nombre = 'Los Testers'
         JOIN equipo e2 ON e2.nombre = 'Los Devs'
WHERE u.email = 'test@unlam.edu.ar'
  AND NOT EXISTS (
    SELECT 1 FROM Partido p
    WHERE p.organizador_id = u.id AND p.fecha = '2026-11-06 20:00:00'
);

SET @partido_test_3 = (
    SELECT id FROM Partido
    WHERE fecha = '2026-11-06 20:00:00'
      AND organizador_id = (
          SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'
      )
);

INSERT INTO InscripcionPartido (
    partido_id,
    jugador_id,
    equipo_id,
    confirmado,
    esCapitan,
    fechaInscripcion,
    recordatorioEnviado
)
SELECT
    p.id,
    j.id,
    e.id,
    true,
    true,
    '2026-11-05 18:00:00',
    false
FROM Partido p
         JOIN Usuario u ON p.organizador_id = u.id
         JOIN Jugador j ON j.usuario_id = u.id
         JOIN equipo e ON e.nombre = 'Los Testers'
WHERE u.email = 'test@unlam.edu.ar'
  AND p.fecha = '2026-11-06 20:00:00'
  AND NOT EXISTS (
    SELECT 1 FROM InscripcionPartido ip
    WHERE ip.partido_id = p.id
      AND ip.jugador_id = j.id
);

-- Estadistica individual para el historial del tercer partido, sin duplicarla.
INSERT INTO Estadistica (
    goles, asistencias, amarillas, rojas, puntaje, jugador_id, partido_id
)
SELECT 1, 0, 1, 0, 5, j.id, p.id
FROM Partido p
         JOIN Usuario u ON p.organizador_id = u.id
         JOIN Jugador j ON j.usuario_id = u.id
WHERE u.email = 'test@unlam.edu.ar'
  AND p.fecha = '2026-11-06 20:00:00'
  AND NOT EXISTS (
    SELECT 1 FROM Estadistica es
    WHERE es.jugador_id = j.id AND es.partido_id = p.id
);
-- Mantener los datos del tercer partido aunque la estadistica ya existiera.
UPDATE Estadistica es
JOIN Partido p ON p.id = es.partido_id
SET es.goles = 1, es.asistencias = 0, es.amarillas = 1, es.rojas = 0, es.puntaje = 5
WHERE es.jugador_id = @jugador_test
  AND p.fecha = '2026-11-06 20:00:00';

-- Resultado del tercer partido: empate entre Los Testers y Los Devs (2 - 2).
SET @resultado_test_3 = (
    SELECT resultado_id FROM Partido
    WHERE id = @partido_test_3
);

INSERT INTO Resultado (golesLocal, golesVisitante, fechaRegistro)
SELECT 2, 2, NOW()
    WHERE @resultado_test_3 IS NULL;

SET @resultado_test_3 = IFNULL(@resultado_test_3, LAST_INSERT_ID());

UPDATE Partido
SET resultado_id = @resultado_test_3
WHERE id = @partido_test_3
  AND resultado_id IS NULL;