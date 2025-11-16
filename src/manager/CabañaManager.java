package manager;

import db.DatabaseManager;
import model.Cabaña;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CabañaManager {

    public CabañaManager() {
    }

    //carga el mapa de precios para una cabaña específica.
    //leer la tabla Tarifa
    private Map<Integer, Double> cargarTarifas(int cabaniaId) throws SQLException {
        Map<Integer, Double> precios = new HashMap<>();
        String sql = "SELECT cant_pasajeros, precio_noche FROM Tarifa WHERE cabaniaid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cabaniaId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    precios.put(rs.getInt("cant_pasajeros"), rs.getDouble("precio_noche"));
                }
            }
        }
        return precios;
    }

    //crea un objeto Cabaña completo desde un ResultSet
    //Carga la cabaña y también llama a cargarTarifas()
    private Cabaña crearCabañaDesdeResultSet(ResultSet rs) throws SQLException {
        int idCabaña = rs.getInt("cabaniaid");
        String numero = rs.getString("numero");
        int capacidad = rs.getInt("capacidad_maxima");
        String estadoStr = rs.getString("estado");
        
        //carga el mapa de precios 
        Map<Integer, Double> precios = cargarTarifas(idCabaña);

        //crea el objeto Cabaña
        Cabaña cabaña = new Cabaña(numero, capacidad, precios);
        cabaña.setCabañaId(idCabaña);

        //ajusta el estado 
        if (estadoStr.equalsIgnoreCase("OCUPADA")) {
            cabaña.marcarOcupada();
        }
        // (Podríamos añadir MANTENIMIENTO si lo usamos)

        return cabaña;
    }

    //obtiene las cabañas de la base de datos
    public List<Cabaña> obtenerTodasLasCabañas() {
        List<Cabaña> cabañas = new ArrayList<>();
        String sql = "SELECT * FROM Cabania";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cabañas.add(crearCabañaDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todas las cabañas:");
            e.printStackTrace();
        }
        return cabañas;
    }

    //obtiene las cabañas que están DISPONIBLES.
    public List<Cabaña> obtenerCabañasDisponibles() {
        List<Cabaña> cabañas = new ArrayList<>();
        String sql = "SELECT * FROM Cabania WHERE estado = 'DISPONIBLE'";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cabañas.add(crearCabañaDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cabañas disponibles:");
            e.printStackTrace();
        }
        return cabañas;
    }

    //busca una cabaña por su ID.
    public Cabaña buscarCabañaPorId(int id) {
        String sql = "SELECT * FROM Cabania WHERE cabaniaid = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return crearCabañaDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cabaña por ID:");
            e.printStackTrace();
        }
        return null; 
    }

    //busca una cabaña por su nombre
    public Cabaña buscarCabañaPorNumero(String numero) {
        String sql = "SELECT * FROM Cabania WHERE numero = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numero);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return crearCabañaDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cabaña por Número:");
            e.printStackTrace();
        }
        return null;
    }
    

    
    //actualiza el estado de una cabaña en la base de datos
    public void actualizarEstadoCabaña(int cabaniaId, model.EstadoCabaña estado) {
        String sql = "UPDATE Cabania SET estado = ? WHERE cabaniaid = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 
            pstmt.setString(1, estado.toString()); // "OCUPADA" o "DISPONIBLE"
            pstmt.setInt(2, cabaniaId);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de cabaña:");
            e.printStackTrace();
        }
    }
}