package manager;

import db.DatabaseManager;
import model.Estadia;
import model.Reserva;
import model.Pago;
import model.EstadoCabaña;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//gestiona el check-in y check-out desde la base de datos
public class EstadiaManager {

    //referencias a otros managers
    private CabañaManager cabañaManager;
    private PagoManager pagoManager;

    public EstadiaManager(CabañaManager cabañaManager, PagoManager pagoManager) {
        this.cabañaManager = cabañaManager;
        this.pagoManager = pagoManager;
    }

    //registra el Check-in en la base de datos
    public Estadia registrarCheckIn(Reserva reserva) throws SQLException {
        //crea el objeto estadia y setear su fecha
        Estadia estadia = new Estadia(reserva);
        estadia.registrarCheckIn(); //pone la fecha actual en el objeto
        
        //inserta en la base de datos
        String sql = "INSERT INTO Estadia (reservaid, checkin) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reserva.getReservaId());
            pstmt.setDate(2, new java.sql.Date(estadia.getCheckIn().getTime()));
            pstmt.executeUpdate();
        }
        
        //actualiza el estado de la cabaña en la base de datos
        cabañaManager.actualizarEstadoCabaña(reserva.getCabaña().getCabañaId(), EstadoCabaña.OCUPADA);
        
        return estadia;
    }

    //registra el Check-out en la base de datos
    public Pago registrarCheckOut(Estadia estadia) throws SQLException {
        //pone la fecha de check-out en el objeto
        estadia.registrarCheckOut();
        
        //actualiza la etadia en la base de datos
        String sql = "UPDATE Estadia SET checkout = ? WHERE reservaid = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, new java.sql.Date(estadia.getCheckOut().getTime()));
            pstmt.setInt(2, estadia.getReserva().getReservaId());
            pstmt.executeUpdate();
        }
        
        //atualiza el estado de la cabaña en la base de datos
        cabañaManager.actualizarEstadoCabaña(estadia.getReserva().getCabaña().getCabañaId(), EstadoCabaña.DISPONIBLE);
        
        String sqlReserva = "UPDATE Reserva SET estado = 'FINALIZADA' WHERE reservaid = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sqlReserva)) {

            pstmt.setInt(1, estadia.getReserva().getReservaId());
            pstmt.executeUpdate();
        }
        
        //finaliza la reserva
        estadia.getReserva().finalizarReserva();
        
        //crear y guarda el pago 
        Pago pago = new Pago(estadia.getReserva());
        pago.calcularTotal(); //calcula el total
        pagoManager.crearPago(pago, estadia.getReserva().getReservaId()); 
        
        return pago;
    }

    //carga una estadía desde la base de datos para una reserva dada
    public Estadia cargarEstadiaParaReserva(Reserva reserva) {
        String sql = "SELECT * FROM Estadia WHERE reservaid = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reserva.getReservaId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Estadia estadia = new Estadia(reserva);
                    estadia.setCheckIn(rs.getDate("checkin"));
                    estadia.setCheckOut(rs.getDate("checkout"));
                    return estadia;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar estadía para reserva " + reserva.getReservaId());
            e.printStackTrace();
        }
        return null;
    }
}