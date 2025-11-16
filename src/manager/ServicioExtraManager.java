package manager;

import db.DatabaseManager;
import model.ServicioExtra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//gstiona los servicios extras desde la base de datos
public class ServicioExtraManager {

    public ServicioExtraManager() {
    }

    private ServicioExtra crearServicioDesdeResultSet(ResultSet rs) throws SQLException {
        ServicioExtra servicio = new ServicioExtra(
            rs.getString("descripcion"),
            rs.getDouble("precio")
        );
        servicio.setServicioId(rs.getInt("servicioid"));
        return servicio;
    }

    //devuelve la lista de todos los servicios que se pueden contratar
    public List<ServicioExtra> obtenerTodosLosServicios() {
        List<ServicioExtra> servicios = new ArrayList<>();
        String sql = "SELECT * FROM ServicioExtra";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                servicios.add(crearServicioDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todos los servicios:");
            e.printStackTrace();
        }
        return servicios;
    }

    //busca un servicio por su ID
    public ServicioExtra buscarServicioPorId(int id) {
        String sql = "SELECT * FROM ServicioExtra WHERE servicioid = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return crearServicioDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar servicio por ID:");
            e.printStackTrace();
        }
        return null;
    }
}