-- Inserta el usuario de prueba solo si no existe previamente
INSERT INTO Usuario (id, email, password, rol, activo)
SELECT null, 'test@unlam.edu.ar', 'test', 'ADMIN', true
WHERE NOT EXISTS (
    SELECT 1 FROM Usuario WHERE email = 'test@unlam.edu.ar'
);

INSERT INTO Cancha(id, nombre, localidad)
VALUES(null, 'Cancha Los Leones', 'San Justo');
INSERT INTO Cancha(id, nombre, localidad)
VALUES(null, 'Cancha 5', 'San Justo');
INSERT INTO Cancha(id, nombre, localidad)
VALUES(null, 'Cancha El Potrero', 'Laferrere');
INSERT INTO Cancha(id, nombre, localidad)
VALUES(null, 'Cancha Los Pibes', 'Laferrere');
INSERT INTO Cancha(id, nombre, localidad)
VALUES(null, 'Cancha La Quinta', 'Ramos Mejia');
INSERT INTO Cancha(id, nombre, localidad)
VALUES(null, 'Cancha El Fortin', 'Ramos Mejia');

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
           0,
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
