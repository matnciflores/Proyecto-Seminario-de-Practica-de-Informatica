USE LikeHome;

-- TABLA: Cliente
CREATE TABLE Cliente (
    clienteid INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    dni VARCHAR(15) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100)
);

-- TABLA: Cabaña
CREATE TABLE Cabania (
    cabaniaid INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(10) NOT NULL,
    estado ENUM('Disponible', 'Ocupada', 'Mantenimiento') DEFAULT 'Disponible'
);

-- TABLA: Reserva
CREATE TABLE Reserva (
    reservaid INT AUTO_INCREMENT PRIMARY KEY,
    clienteid INT NOT NULL,
    cabaniaid INT NOT NULL,
    fechainicio DATE NOT NULL,
    fechafin DATE NOT NULL,
    estado ENUM('Activa', 'Cancelada', 'Finalizada') DEFAULT 'Activa',
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (clienteid) REFERENCES Cliente(clienteid),
    CONSTRAINT fk_reserva_cabania FOREIGN KEY (cabaniaid) REFERENCES Cabania(cabaniaid)
);

-- TABLA: Estadía
CREATE TABLE Estadia (
    estadiaid INT AUTO_INCREMENT PRIMARY KEY,
    reservaid INT UNIQUE NOT NULL,
    checkin DATE,
    checkout DATE,
    CONSTRAINT fk_estadia_reserva FOREIGN KEY (reservaid) REFERENCES Reserva(reservaid)
);

-- TABLA: ServicioExtra
CREATE TABLE ServicioExtra (
    servicioid INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL
);


-- TABLA Reserva_ServicioExtra
CREATE TABLE Reserva_ServicioExtra (
    reservaid INT NOT NULL,
    servicioid INT NOT NULL,
    PRIMARY KEY (reservaid, servicioid),
    CONSTRAINT fk_rs_reserva FOREIGN KEY (reservaid) REFERENCES Reserva(reservaid),
    CONSTRAINT fk_rs_servicio FOREIGN KEY (servicioid) REFERENCES ServicioExtra(servicioid)
);

-- TABLA: Pago
CREATE TABLE Pago (
    pagoid INT AUTO_INCREMENT PRIMARY KEY,
    reservaid INT UNIQUE NOT NULL,
    montototal DECIMAL(10,2) NOT NULL,
    fecha DATE NOT NULL,
    CONSTRAINT fk_pago_reserva FOREIGN KEY (reservaid) REFERENCES Reserva(reservaid)
);

-- TABLA: Reporte
CREATE TABLE Reporte (
    reporteid INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    periodo VARCHAR(50),
    fecha_generacion DATETIME DEFAULT CURRENT_TIMESTAMP
);