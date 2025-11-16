package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//gestion la conexión a la base de datos MySQL.
public class DatabaseManager {
    //configuración de la conexión a la base de datos
    //base de datos 'likehome'
    private static final String DB_URL = "jdbc:mysql://localhost:3306/likehome";
    private static final String USER = "root"; 
    private static final String PASS = ""; 

    //establece y devuelve una conexión a la base de datos.
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: No se encontró el driver JDBC de MySQL.", e);
        }
        
        //establecer la conexión
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    //metodo para probar la conexión
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                System.out.println("La conexión a la base de datos 'likehome' fue exitosa!");
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Falló la conexión a la base de datos.");
            e.printStackTrace();
        }
    }
}