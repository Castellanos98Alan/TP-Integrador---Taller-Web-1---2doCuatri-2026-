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
SELECT 'Jugador Test', 'Delantero', 'Mediocampista', 60, 'A', u.id, 3, 2, 1
FROM Usuario u
WHERE u.email = 'test@unlam.edu.ar'
  AND NOT EXISTS (SELECT 1 FROM Jugador j WHERE j.usuario_id = u.id);

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

-- Inscripcion del Jugador Test en ese partido.

INSERT INTO InscripcionPartido (
    partido_id,
    jugador_id,
    equipo_id,
    confirmado,
    esCapitan,
    fechaInscripcion,
    recordatorioEnviado
)
VALUES (
           @partido_test,
           @jugador_test,
           @equipo_testers,
           true,
           true,
           '2026-10-29 18:00:00',
           false
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