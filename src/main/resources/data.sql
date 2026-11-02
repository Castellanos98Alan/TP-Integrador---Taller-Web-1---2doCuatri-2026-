-- Inserta el usuario ADMIN de prueba
INSERT INTO Usuario (id, email, password, rol, activo)
SELECT null, 'test@unlam.edu.ar', 'test', 'ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'test@unlam.edu.ar');

-- Crea usuarios para el resto de los jugadores (contraseña general: 1234)
INSERT INTO Usuario (id, email, password, rol, activo)
SELECT null, 'alan@unlam.edu.ar', '1234', 'USER', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'alan@unlam.edu.ar');

INSERT INTO Usuario (id, email, password, rol, activo)
SELECT null, 'axel@unlam.edu.ar', '1234', 'USER', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'axel@unlam.edu.ar');

INSERT INTO Usuario (id, email, password, rol, activo)
SELECT null, 'santi@unlam.edu.ar', '1234', 'USER', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'santi@unlam.edu.ar');

INSERT INTO Usuario (id, email, password, rol, activo)
SELECT null, 'juanpa@unlam.edu.ar', '1234', 'USER', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'juanpa@unlam.edu.ar');

INSERT INTO Usuario (id, email, password, rol, activo)
SELECT null, 'tomas@unlam.edu.ar', '1234', 'USER', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'tomas@unlam.edu.ar');

-- Inserta las canchas
INSERT INTO Cancha(id, nombre, localidad) VALUES(null, 'Cancha Los Leones', 'San Justo');
INSERT INTO Cancha(id, nombre, localidad) VALUES(null, 'Cancha 5', 'San Justo');
INSERT INTO Cancha(id, nombre, localidad) VALUES(null, 'Cancha El Potrero', 'Laferrere');
INSERT INTO Cancha(id, nombre, localidad) VALUES(null, 'Cancha Los Pibes', 'Laferrere');
INSERT INTO Cancha(id, nombre, localidad) VALUES(null, 'Cancha La Quinta', 'Ramos Mejia');
INSERT INTO Cancha(id, nombre, localidad) VALUES(null, 'Cancha El Fortin', 'Ramos Mejia');

-- Inserta los jugadores vinculándolos a sus respectivos usuarios
INSERT INTO Jugador(nombre, posicion1, posicion2, puntos, nivel, usuario_id)
VALUES('Alan', 'Delantero', 'Arquero', 40, 'B', (SELECT id FROM Usuario WHERE email = 'alan@unlam.edu.ar' LIMIT 1));

INSERT INTO Jugador(nombre, posicion1, posicion2, puntos, nivel, usuario_id)
VALUES('Axel', 'Delantero', 'Arquero', 50, 'A', (SELECT id FROM Usuario WHERE email = 'axel@unlam.edu.ar' LIMIT 1));

INSERT INTO Jugador(nombre, posicion1, posicion2, puntos, nivel, usuario_id)
VALUES('Santi', 'Defensor', 'Mediocampista', 70, 'A', (SELECT id FROM Usuario WHERE email = 'santi@unlam.edu.ar' LIMIT 1));

INSERT INTO Jugador(nombre, posicion1, posicion2, puntos, nivel, usuario_id)
VALUES('JuanPa', 'Arquero', 'Defensor', 20, 'C', (SELECT id FROM Usuario WHERE email = 'juanpa@unlam.edu.ar' LIMIT 1));

INSERT INTO Jugador(nombre, posicion1, posicion2, puntos, nivel, usuario_id)
VALUES('Tomas', 'Defensor', 'Arquero', 0, 'D', (SELECT id FROM Usuario WHERE email = 'tomas@unlam.edu.ar' LIMIT 1));

-- Crear un jugador exclusivo para el usuario de prueba ADMIN
INSERT INTO Jugador (nombre, posicion1, posicion2, puntos, nivel, usuario_id, partidosJugados, partidosGanados, partidosPerdidos)
VALUES ('Jugador Test', 'Delantero', 'Mediocampista', 60, 'A', (SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar' LIMIT 1), 3, 2, 1);

-- Cargar estadísticas de ejemplo para ese jugador ADMIN
INSERT INTO Estadistica (goles, asistencias, amarillas, rojas, puntaje, jugador_id, partido_id)
VALUES (3, 2, 0, 0, 8, (SELECT id FROM Jugador WHERE usuario_id = (SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar' LIMIT 1) LIMIT 1), NULL);