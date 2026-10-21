INSERT INTO Usuario(id, email, password, rol, activo)
VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true);


INSERT INTO Cancha(id, nombre, provincia, localidad)
VALUES(null, 'Cancha Los Leones', 'BUENOS AIRES', 'LA MATANZA');
INSERT INTO Cancha(id, nombre, provincia, localidad)
VALUES(null, 'Cancha 5','BUENOS AIRES', 'LA MATANZA');
INSERT INTO Cancha(id, nombre, provincia, localidad)
VALUES(null, 'Cancha El Potrero','BUENOS AIRES', 'LA MATANZA');

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
           (SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'),
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
                   WHERE email = 'test@unlam.edu.ar'
               )
           ),
           NULL
       );
