-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 17-11-2025 a las 02:53:09
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `likehome`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cabania`
--

CREATE TABLE `cabania` (
  `cabaniaid` int(11) NOT NULL,
  `numero` varchar(10) NOT NULL,
  `capacidad_maxima` int(11) NOT NULL,
  `estado` enum('Disponible','Ocupada','Mantenimiento') DEFAULT 'Disponible'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `cabania`
--

INSERT INTO `cabania` (`cabaniaid`, `numero`, `capacidad_maxima`, `estado`) VALUES
(1, 'Kurmi', 4, 'Ocupada'),
(2, 'Sumaq', 4, 'Disponible'),
(3, 'Qinti', 2, 'Disponible');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `clienteid` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `dni` varchar(15) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`clienteid`, `nombre`, `apellido`, `dni`, `telefono`, `email`) VALUES
(1, 'Juan', 'Pérez', '30111222', '1122334455', 'juan.perez@email.com'),
(2, 'María', 'González', '29444555', '1133445566', 'maria.gonzalez@email.com'),
(3, 'Carlos', 'López', '31122333', '1144556699', 'carlos.lopez@email.com'),
(4, 'Lucía', 'Fernández', '27666777', '1155667788', 'lucia.fernandez@email.com'),
(5, 'Sofía', 'Martínez', '32233444', '1166778899', 'sofia.martinez@email.com'),
(6, 'Matias', 'Flores', '23345345', '268993939', 'mati@gmail.com'),
(7, 'Claudio', 'Flores', '12344323', '233456790', 'clau@gmail.com'),
(8, 'Marcos', 'Alonso', '12345678', '122345690', 'marcos@gmail.com'),
(9, 'Jaime', 'Rosales', '12345789', '2334565890', 'jaime@gmail.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estadia`
--

CREATE TABLE `estadia` (
  `estadiaid` int(11) NOT NULL,
  `reservaid` int(11) NOT NULL,
  `checkin` date DEFAULT NULL,
  `checkout` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `estadia`
--

INSERT INTO `estadia` (`estadiaid`, `reservaid`, `checkin`, `checkout`) VALUES
(1, 13, '2025-11-16', '2025-11-16'),
(2, 14, '2025-11-16', '2025-11-16'),
(3, 15, '2025-11-16', '2025-11-16'),
(4, 16, '2025-11-16', '2025-11-16'),
(5, 17, '2025-11-16', '2025-11-16'),
(6, 18, '2025-11-16', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pago`
--

CREATE TABLE `pago` (
  `pagoid` int(11) NOT NULL,
  `reservaid` int(11) NOT NULL,
  `montototal` decimal(10,2) NOT NULL,
  `fecha` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pago`
--

INSERT INTO `pago` (`pagoid`, `reservaid`, `montototal`, `fecha`) VALUES
(1, 13, 268000.00, '2025-11-16'),
(2, 14, 260000.00, '2025-11-16'),
(3, 15, 118000.00, '2025-11-16'),
(4, 16, 198000.00, '2025-11-16'),
(5, 17, 188000.00, '2025-11-16');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reserva`
--

CREATE TABLE `reserva` (
  `reservaid` int(11) NOT NULL,
  `clienteid` int(11) NOT NULL,
  `cabaniaid` int(11) NOT NULL,
  `fechainicio` date NOT NULL,
  `fechafin` date NOT NULL,
  `estado` enum('Activa','Cancelada','Finalizada') DEFAULT 'Activa',
  `cantidad_pasajeros` int(11) NOT NULL,
  `precio_noche_final` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reserva`
--

INSERT INTO `reserva` (`reservaid`, `clienteid`, `cabaniaid`, `fechainicio`, `fechafin`, `estado`, `cantidad_pasajeros`, `precio_noche_final`) VALUES
(13, 6, 2, '2025-12-20', '2025-12-30', 'Finalizada', 4, 25000.00),
(14, 2, 3, '2026-01-01', '2026-01-10', 'Finalizada', 2, 28000.00),
(15, 7, 1, '2025-12-25', '2025-12-30', 'Finalizada', 4, 20000.00),
(16, 8, 1, '2026-01-01', '2026-01-10', 'Finalizada', 4, 20000.00),
(17, 7, 1, '2026-02-01', '2026-02-10', 'Finalizada', 4, 20000.00),
(18, 9, 1, '2026-02-20', '2026-02-25', 'Activa', 4, 20000.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reserva_servicioextra`
--

CREATE TABLE `reserva_servicioextra` (
  `reservaid` int(11) NOT NULL,
  `servicioid` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reserva_servicioextra`
--

INSERT INTO `reserva_servicioextra` (`reservaid`, `servicioid`) VALUES
(13, 1),
(13, 2),
(14, 1),
(15, 1),
(15, 2),
(16, 1),
(16, 2),
(17, 1),
(18, 2),
(18, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `servicioextra`
--

CREATE TABLE `servicioextra` (
  `servicioid` int(11) NOT NULL,
  `descripcion` varchar(100) NOT NULL,
  `precio` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `servicioextra`
--

INSERT INTO `servicioextra` (`servicioid`, `descripcion`, `precio`) VALUES
(1, 'Desayunos', 8000.00),
(2, 'Excursión a las Sierras', 10000.00),
(3, 'Excursión a Caballo', 13000.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarifa`
--

CREATE TABLE `tarifa` (
  `tarifaid` int(11) NOT NULL,
  `cabaniaid` int(11) NOT NULL,
  `cant_pasajeros` int(11) NOT NULL,
  `precio_noche` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tarifa`
--

INSERT INTO `tarifa` (`tarifaid`, `cabaniaid`, `cant_pasajeros`, `precio_noche`) VALUES
(1, 1, 2, 15000.00),
(2, 1, 3, 18000.00),
(3, 1, 4, 20000.00),
(4, 2, 2, 20000.00),
(5, 2, 3, 23000.00),
(6, 2, 4, 25000.00),
(7, 3, 1, 26000.00),
(8, 3, 2, 28000.00);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `cabania`
--
ALTER TABLE `cabania`
  ADD PRIMARY KEY (`cabaniaid`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`clienteid`),
  ADD UNIQUE KEY `dni` (`dni`);

--
-- Indices de la tabla `estadia`
--
ALTER TABLE `estadia`
  ADD PRIMARY KEY (`estadiaid`),
  ADD UNIQUE KEY `reservaid` (`reservaid`);

--
-- Indices de la tabla `pago`
--
ALTER TABLE `pago`
  ADD PRIMARY KEY (`pagoid`),
  ADD UNIQUE KEY `reservaid` (`reservaid`);

--
-- Indices de la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD PRIMARY KEY (`reservaid`),
  ADD KEY `fk_reserva_cliente` (`clienteid`),
  ADD KEY `fk_reserva_cabania` (`cabaniaid`);

--
-- Indices de la tabla `reserva_servicioextra`
--
ALTER TABLE `reserva_servicioextra`
  ADD PRIMARY KEY (`reservaid`,`servicioid`),
  ADD KEY `fk_rs_servicio` (`servicioid`);

--
-- Indices de la tabla `servicioextra`
--
ALTER TABLE `servicioextra`
  ADD PRIMARY KEY (`servicioid`);

--
-- Indices de la tabla `tarifa`
--
ALTER TABLE `tarifa`
  ADD PRIMARY KEY (`tarifaid`),
  ADD KEY `cabaniaid` (`cabaniaid`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `cabania`
--
ALTER TABLE `cabania`
  MODIFY `cabaniaid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `clienteid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `estadia`
--
ALTER TABLE `estadia`
  MODIFY `estadiaid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `pago`
--
ALTER TABLE `pago`
  MODIFY `pagoid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `reserva`
--
ALTER TABLE `reserva`
  MODIFY `reservaid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `servicioextra`
--
ALTER TABLE `servicioextra`
  MODIFY `servicioid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tarifa`
--
ALTER TABLE `tarifa`
  MODIFY `tarifaid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `estadia`
--
ALTER TABLE `estadia`
  ADD CONSTRAINT `fk_estadia_reserva` FOREIGN KEY (`reservaid`) REFERENCES `reserva` (`reservaid`);

--
-- Filtros para la tabla `pago`
--
ALTER TABLE `pago`
  ADD CONSTRAINT `fk_pago_reserva` FOREIGN KEY (`reservaid`) REFERENCES `reserva` (`reservaid`);

--
-- Filtros para la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD CONSTRAINT `fk_reserva_cabania` FOREIGN KEY (`cabaniaid`) REFERENCES `cabania` (`cabaniaid`),
  ADD CONSTRAINT `fk_reserva_cliente` FOREIGN KEY (`clienteid`) REFERENCES `cliente` (`clienteid`);

--
-- Filtros para la tabla `reserva_servicioextra`
--
ALTER TABLE `reserva_servicioextra`
  ADD CONSTRAINT `fk_rs_reserva` FOREIGN KEY (`reservaid`) REFERENCES `reserva` (`reservaid`),
  ADD CONSTRAINT `fk_rs_servicio` FOREIGN KEY (`servicioid`) REFERENCES `servicioextra` (`servicioid`);

--
-- Filtros para la tabla `tarifa`
--
ALTER TABLE `tarifa`
  ADD CONSTRAINT `tarifa_ibfk_1` FOREIGN KEY (`cabaniaid`) REFERENCES `cabania` (`cabaniaid`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
