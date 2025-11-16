package manager;

import db.DatabaseManager;
import model.Pago;
import model.Reserva;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//gestiona la creación y carga de pagos desde la base de datos
public class PagoManager {

    public PagoManager() {
    }

    //crea un nuevo pago en la base de datos
    public void crearPago(Pago pago, int reservaId) throws SQLException {
        String sql = "INSERT INTO Pago (reservaid, montototal, fecha) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, reservaId);
            pstmt.setDouble(2, pago.getMontoTotal());
            pstmt.setDate(3, new java.sql.Date(pago.getFecha().getTime()));
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Fallo al crear el pago, ninguna fila afectada.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pago.setPagoId(generatedKeys.getInt(1)); // Asigna el ID de la BD
                    System.out.println("Pago creado con ID: " + pago.getPagoId());
                } else {
                    throw new SQLException("Fallo al crear el pago, no se obtuvo ID.");
                }
            }
        }
    }

    //carga un pago desde la base dedatos para una reserva dada
    //usado por ReservaManager al cargar una reserva completa
    public Pago cargarPagoParaReserva(Reserva reserva) {
        String sql = "SELECT * FROM Pago WHERE reservaid = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reserva.getReservaId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pago pago = new Pago(reserva);
                    pago.setPagoId(rs.getInt("pagoid"));
                    //seteamos los datos guardados
                    pago.setMontoTotal(rs.getDouble("montototal")); 
                    pago.setFecha(rs.getDate("fecha"));
                    return pago;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar pago para reserva " + reserva.getReservaId());
            e.printStackTrace();
        }
        return null; 
    }
}