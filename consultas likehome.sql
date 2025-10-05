USE likehome;

INSERT INTO Cliente (nombre, apellido, dni, telefono, email) VALUES
('Juan', 'Pérez', '30111222', '1122334455', 'juan.perez@email.com'),
('María', 'González', '29444555', '1133445566', 'maria.gonzalez@email.com'),
('Carlos', 'López', '31122333', '1144556677', 'carlos.lopez@email.com'),
('Lucía', 'Fernández', '27666777', '1155667788', 'lucia.fernandez@email.com'),
('Sofía', 'Martínez', '32233444', '1166778899', 'sofia.martinez@email.com');

INSERT INTO Cabania (numero, estado) VALUES
('C1', 'Disponible'),
('C2', 'Disponible'),
('C3', 'Ocupada');

INSERT INTO ServicioExtra (descripcion, precio) VALUES
('Desayuno', 1500.00),
('Excursión', 5000.00),
('Limpieza de cabaña', 2000.00);


INSERT INTO Reserva (clienteid, cabaniaid, fechainicio, fechafin, estado) VALUES
(1, 1, '2025-09-01', '2025-09-05', 'Finalizada'),
(2, 2, '2025-09-10', '2025-09-15', 'Finalizada'),
(3, 3, '2025-10-01', '2025-10-07', 'Activa'),
(4, 1, '2025-10-08', '2025-10-12', 'Activa'),
(5, 2, '2025-11-01', '2025-11-05', 'Activa'),
(1, 3, '2025-12-01', '2025-12-10', 'Cancelada');

INSERT INTO Estadia (reservaid, checkin, checkout) VALUES
(1, '2025-09-01', '2025-09-05'),
(2, '2025-09-10', '2025-09-15');

INSERT INTO Pago (reservaid, montototal, fecha) VALUES
(1, 25000.00, '2025-09-05'),
(2, 32000.00, '2025-09-15'),
(3, 28000.00, '2025-10-01'),
(4, 24000.00, '2025-10-08'),
(5, 26000.00, '2025-11-01'),
(6, 40000.00, '2025-12-01');

INSERT INTO Reserva_ServicioExtra (reservaid, servicioid) VALUES
(1, 1), -- Desayuno
(1, 3), -- Limpieza
(2, 2), -- Excursión
(3, 1), -- Desayuno
(3, 2), -- Excursión
(4, 3), -- Limpieza
(5, 1), -- Desayuno
(6, 2); -- Excursión

SELECT R.reservaid, R.fechainicio, R.fechafin, R.estado, C.numero AS cabania
FROM Reserva R
JOIN Cabania C ON R.cabaniaid = C.cabaniaid
WHERE R.clienteid = 1;

SELECT R.reservaid, Cl.nombre, Cl.apellido, C.numero AS cabania, R.fechainicio, R.fechafin
FROM Reserva R
JOIN Cliente Cl ON R.clienteid = Cl.clienteid
JOIN Cabania C ON R.cabaniaid = C.cabaniaid
WHERE R.estado = 'Activa';

SELECT Cl.nombre, Cl.apellido, COUNT(R.reservaid) AS total_reservas
FROM Cliente Cl
LEFT JOIN Reserva R ON Cl.clienteid = R.clienteid
GROUP BY Cl.clienteid;

SELECT C.cabaniaid, C.numero, C.estado
FROM Cabania C
WHERE C.cabaniaid NOT IN (
    SELECT R.cabaniaid
    FROM Reserva R
    WHERE ('2025-10-05' BETWEEN R.fechainicio AND R.fechafin)
      AND R.estado = 'Activa'
);

DELETE FROM Reserva
WHERE estado = 'Cancelada';
SELECT * FROM Reserva;

DELETE FROM Reserva WHERE clienteid = 1;
SELECT * FROM Reserva WHERE clienteid = 1;
