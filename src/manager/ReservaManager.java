package manager;

import db.DatabaseManager;
import model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservaManager {

    private ClienteManager clienteManager;
    private CabañaManager cabañaManager;
    private ServicioExtraManager servicioExtraManager;
    private EstadiaManager estadiaManager;
    private PagoManager pagoManager;

    public ReservaManager(ClienteManager cm, CabañaManager cabm, ServicioExtraManager sem, EstadiaManager em, PagoManager pm) {
        this.clienteManager = cm;
        this.cabañaManager = cabm;
        this.servicioExtraManager = sem;
        this.estadiaManager = em;
        this.pagoManager = pm;
    }

    //valida disponibilidad de cabaña
    private boolean isCabañaDisponible(Cabaña cabaña, Date fechaInicio, Date fechaFin, int idReservaExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Reserva " + "WHERE cabaniaid = ? " + "AND reservaid != ? " + "AND estado = 'ACTIVA' " + "AND (? < fechafin AND ? > fechainicio)"; //logica de superposición
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cabaña.getCabañaId());
            pstmt.setInt(2, idReservaExcluir);
            pstmt.setDate(3, new java.sql.Date(fechaInicio.getTime()));
            pstmt.setDate(4, new java.sql.Date(fechaFin.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }

    //crea una nueva reserva en la base de datos
    public void crearReserva(Reserva reserva) throws Exception {
        
        if (!isCabañaDisponible(reserva.getCabaña(), reserva.getFechaInicio(), reserva.getFechaFin(), 0)) {
            throw new Exception("La cabaña no está disponible en esas fechas.");
        }

        //transacción para asegurar que la reserva y los servicios se guarden
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            //insertar en la tabla Reserva
            String sqlReserva = "INSERT INTO Reserva (clienteid, cabaniaid, fechainicio, fechafin, estado, cantidad_pasajeros, precio_noche_final) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            int reservaId;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, reserva.getCliente().getClienteId());
                pstmt.setInt(2, reserva.getCabaña().getCabañaId());
                pstmt.setDate(3, new java.sql.Date(reserva.getFechaInicio().getTime()));
                pstmt.setDate(4, new java.sql.Date(reserva.getFechaFin().getTime()));
                pstmt.setString(5, reserva.getEstado().toString());
                pstmt.setInt(6, reserva.getCantidadPasajeros());
                pstmt.setDouble(7, reserva.getPrecioFinalPorNoche());
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservaId = generatedKeys.getInt(1);
                        reserva.setReservaId(reservaId); //asigna el nuevo ID
                    } else {
                        throw new SQLException("Fallo al crear la reserva. No se obtuvo ID.");
                    }
                }
            }

            //inserta en la tabla 'reserva_servicioextra' 
            String sqlServicios = "INSERT INTO reserva_servicioextra (reservaid, servicioid) VALUES (?, ?)";
            try (PreparedStatement pstmtServ = conn.prepareStatement(sqlServicios)) {
                for (ServicioExtra s : reserva.getServiciosContratados()) {
                    pstmtServ.setInt(1, reservaId);
                    pstmtServ.setInt(2, s.getServicioId());
                    pstmtServ.addBatch();
                }
                pstmtServ.executeBatch(); //ejecuta
            }

            conn.commit(); //confirmar transaccion
            System.out.println("Reserva creada con éxito: ID " + reserva.getReservaId());

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); //deshacer si algo falló
            System.err.println("Error al crear reserva:");
            e.printStackTrace();
            throw new Exception("Error al crear reserva.");
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    //modifica una reserva existente en la base de datos
    public void modificarReserva(int idReserva, Cabaña nuevaCabaña, Date nuevaFechaInicio, Date nuevaFechaFin, int nuevaCantPasajeros, List<ServicioExtra> nuevosServicios) throws Exception {
        
        Reserva reservaAModificar = this.buscarReservaPorId(idReserva);
        if (reservaAModificar == null) throw new Exception("No se encontró la reserva.");
        if (reservaAModificar.getEstado() != EstadoReserva.ACTIVA) throw new Exception("Solo se pueden modificar reservas 'ACTIVAS'.");
        if (reservaAModificar.getEstadia() != null && reservaAModificar.getEstadia().getCheckIn() != null) throw new Exception("No se puede modificar una reserva con Check-in.");
       
        if (!isCabañaDisponible(nuevaCabaña, nuevaFechaInicio, nuevaFechaFin, idReserva)) {
            throw new Exception("La cabaña no está disponible en las fechas seleccionadas.");
        }
        
        if (nuevaCantPasajeros > nuevaCabaña.getCapacidad()) throw new Exception("Cantidad de pasajeros excede la capacidad.");
        
        double nuevoPrecioNoche = nuevaCabaña.getPrecioPorNoche(nuevaCantPasajeros);

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            
            //actualiza la tabla Reserva
            String sqlUpdate = "UPDATE Reserva SET cabaniaid = ?, fechainicio = ?, fechafin = ?, " + "cantidad_pasajeros = ?, precio_noche_final = ? WHERE reservaid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setInt(1, nuevaCabaña.getCabañaId());
                pstmt.setDate(2, new java.sql.Date(nuevaFechaInicio.getTime()));
                pstmt.setDate(3, new java.sql.Date(nuevaFechaFin.getTime()));
                pstmt.setInt(4, nuevaCantPasajeros);
                pstmt.setDouble(5, nuevoPrecioNoche);
                pstmt.setInt(6, idReserva);
                pstmt.executeUpdate();
            }
            
            //borrar servicios extras 
            String sqlDeleteServ = "DELETE FROM reserva_servicioextra WHERE reservaid = ?";
            try (PreparedStatement pstmtDel = conn.prepareStatement(sqlDeleteServ)) {
                pstmtDel.setInt(1, idReserva);
                pstmtDel.executeUpdate();
            }
            
            //insertar servicios extras
            String sqlInsertServ = "INSERT INTO reserva_servicioextra (reservaid, servicioid) VALUES (?, ?)";
            try (PreparedStatement pstmtIns = conn.prepareStatement(sqlInsertServ)) {
                for (ServicioExtra s : nuevosServicios) {
                    pstmtIns.setInt(1, idReserva);
                    pstmtIns.setInt(2, s.getServicioId());
                    pstmtIns.addBatch();
                }
                pstmtIns.executeBatch();
            }
            
            conn.commit();
            System.out.println("Reserva modificada con éxito: ID " + idReserva);
            
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            e.printStackTrace();
            throw new Exception("Error al modificar reserva.");
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    //cancela una reserva en la base de datos
    public void cancelarReserva(int id) throws Exception {
        Reserva r = this.buscarReservaPorId(id);
        if (r == null) throw new Exception("No se encontró la reserva.");
        if (r.getEstadia() != null && r.getEstadia().getCheckIn() != null) throw new Exception("No se puede cancelar una reserva con Check-in.");
        if (r.getEstado() != EstadoReserva.ACTIVA) throw new Exception("La reserva ya está " + r.getEstado().toString().toLowerCase() + ".");

        String sql = "UPDATE Reserva SET estado = 'CANCELADA' WHERE reservaid = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Reserva cancelada: ID " + id);
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al cancelar reserva.");
        }
    }

    //carga la lista de servicios para una reserva
    private List<ServicioExtra> cargarServiciosParaReserva(int reservaid) throws SQLException {
        List<ServicioExtra> servicios = new ArrayList<>();
        String sql = "SELECT servicioid FROM reserva_servicioextra WHERE reservaid = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reservaid);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ServicioExtra s = servicioExtraManager.buscarServicioPorId(rs.getInt("servicioid"));
                    if (s != null) {
                        servicios.add(s);
                    }
                }
            }
        }
        return servicios;
    }
    
    //método de utilidad para construir un objeto Reserva completo desde la base de datos
    private Reserva crearReservaDesdeResultSet(ResultSet rs) throws Exception {
        Cliente cliente = clienteManager.buscarClientePorId(rs.getInt("clienteid"));
        Cabaña cabaña = cabañaManager.buscarCabañaPorId(rs.getInt("cabaniaid"));
        
        if (cliente == null || cabaña == null) {
            throw new Exception("Error de datos: no se encontró cliente o cabaña para la reserva.");
        }
        
        //creala reserva con los datos leídos
        Reserva reserva = new Reserva(
            cliente,
            cabaña,
            rs.getDate("fechainicio"),
            rs.getDate("fechafin"),
            rs.getInt("cantidad_pasajeros")
        );
       
        reserva.setReservaId(rs.getInt("reservaid"));
        reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado").toUpperCase()));
        reserva.setPrecioFinalPorNoche(rs.getDouble("precio_noche_final"));
        
        //cargar relaciones complejas
        List<ServicioExtra> servicios = cargarServiciosParaReserva(reserva.getReservaId());
        for (ServicioExtra s : servicios) {
            reserva.agregarServicio(s);
        }
        
        Estadia estadia = estadiaManager.cargarEstadiaParaReserva(reserva);
        if (estadia != null) {
            reserva.setEstadia(estadia);
        }
        
        Pago pago = pagoManager.cargarPagoParaReserva(reserva);
        if (pago != null) {
            reserva.setPago(pago);
        }
        
        return reserva;
    }

    //busca una reserva por su ID
    public Reserva buscarReservaPorId(int id) {
        String sql = "SELECT * FROM Reserva WHERE reservaid = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return crearReservaDesdeResultSet(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar reserva por ID:");
            e.printStackTrace();
        }
        return null;
    }

    //obtiene todas las reservas activas
    public List<Reserva> obtenerReservasActivas() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE estado = 'ACTIVA'";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                try {
                    reservas.add(crearReservaDesdeResultSet(rs));
                } catch (Exception e) {
                    System.err.println("Error al construir reserva activa ID: " + rs.getInt("reservaid"));
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }

    //obtiene todas las reservas para reportes
    public List<Reserva> obtenerTodasLasReservas() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                try {
                    reservas.add(crearReservaDesdeResultSet(rs));
                } catch (Exception e) {
                    System.err.println("Error al construir reserva ID: " + rs.getInt("reservaid"));
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }
    
    //obtiene las reservas Finalizadas cuyo pago se realizó dentro de un rango de fechas específico
    public List<Reserva> obtenerReservasFinalizadasPorFecha(Date fechaDesde, Date fechaHasta) {
        List<Reserva> reservas = new ArrayList<>();
        
        String sql = "SELECT R.* FROM Reserva R " + "JOIN Pago P ON R.reservaid = P.reservaid " +"WHERE R.estado = 'FINALIZADA' " + "AND P.fecha BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
            pstmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        reservas.add(crearReservaDesdeResultSet(rs)); 
                    } catch (Exception e) {
                        System.err.println("Error al construir reserva finalizada ID: " + rs.getInt("reservaid"));
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas por fecha:");
            e.printStackTrace();
        }
        return reservas;
    }
}