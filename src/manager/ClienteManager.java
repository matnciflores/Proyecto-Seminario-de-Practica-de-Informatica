package manager;

import db.DatabaseManager; // <<< 1. Importar nuestra clase de conexión
import model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // <<< 2. Importar clases de SQL
import java.util.ArrayList;
import java.util.List;

public class ClienteManager {

    public ClienteManager() {
    }
    
    //agrega un nuevo cliente a la base de datos
    public void agregarCliente(Cliente cliente) throws Exception {
        //validar DNI duplicado 
        if (buscarClientePorDni(cliente.getDni()) != null) {
            throw new Exception("Error: El DNI " + cliente.getDni() + " ya está registrado.");
        }

        String sql = "INSERT INTO Cliente (nombre, apellido, dni, telefono, email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getDni());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEmail());

            int filasAfectadas = pstmt.executeUpdate(); // Ejecuta el INSERT

            if (filasAfectadas == 0) {
                throw new SQLException("Fallo al crear el cliente, ninguna fila afectada.");
            }

            //obtiene el ID generado por la base de datos
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setClienteId(generatedKeys.getInt(1)); // Asigna el nuevo ID al objeto
                    System.out.println("Cliente agregado con ID: " + cliente.getClienteId());
                } else {
                    throw new SQLException("Fallo al crear el cliente, no se obtuvo ID.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error de SQL al agregar cliente:");
            e.printStackTrace();
            throw new Exception("Error en la base de datos al agregar cliente.");
        }
    }
    
    //modifica un cliente existente en la base de datos
    public void modificarCliente(int id, String nuevoNombre, String nuevoApellido, String nuevoDni, String nuevoTelefono, String nuevoEmail) throws Exception {
        //validar DNI duplicado
        Cliente clienteConEseDni = buscarClientePorDni(nuevoDni);
        if (clienteConEseDni != null && clienteConEseDni.getClienteId() != id) {
            throw new Exception("Error: El DNI " + nuevoDni + " ya pertenece a otro cliente.");
        }
        
        String sql = "UPDATE Cliente SET nombre = ?, apellido = ?, dni = ?, telefono = ?, email = ? " +
                     "WHERE clienteid = ?";
                     
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 
            pstmt.setString(1, nuevoNombre);
            pstmt.setString(2, nuevoApellido);
            pstmt.setString(3, nuevoDni);
            pstmt.setString(4, nuevoTelefono);
            pstmt.setString(5, nuevoEmail);
            pstmt.setInt(6, id);

            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Cliente modificado con ID: " + id);
            } else {
                throw new Exception("No se encontró el cliente con ID " + id + " para modificar.");
            }

        } catch (SQLException e) {
            System.err.println("Error de SQL al modificar cliente:");
            e.printStackTrace();
            throw new Exception("Error en la base de datos al modificar cliente.");
        }
    }

    //buscar cliente por dni
    public Cliente buscarClientePorDni(String dni) {
        String sql = "SELECT * FROM Cliente WHERE dni = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dni);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    //si encontramos un cliente, creamos el objeto
                    return crearClienteDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de SQL al buscar cliente por DNI:");
            e.printStackTrace();
        }
        return null; //no se encontró
    }

    public Cliente buscarClientePorId(int id) {
        String sql = "SELECT * FROM Cliente WHERE clienteid = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return crearClienteDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de SQL al buscar cliente por ID:");
            e.printStackTrace();
        }
        return null;
    }
    //obtiene los clientes de la base de datos.
    public List<Cliente> obtenerTodosLosClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) { // Ejecuta el SELECT

            // Itera sobre cada fila del resultado
            while (rs.next()) {
                clientes.add(crearClienteDesdeResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todos los clientes:");
            e.printStackTrace();
        }
        return clientes;
    }
    
    //crea un objeto Cliente a partir de una fila de ResultSet
    private Cliente crearClienteDesdeResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("dni"),
            rs.getString("telefono"),
            rs.getString("email")
        );
        cliente.setClienteId(rs.getInt("clienteid"));
        return cliente;
    }
}