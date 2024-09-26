/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author egiron
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // URL para conectarse a la base de datos, utilizando JDBC y SQL Server. 
    // Aquí se define la dirección del servidor, el nombre de la base de datos y las propiedades adicionales como la encriptación.
    private static final String URL = "jdbc:sqlserver://localhost;databaseName=AutomataDB;encrypt=false;";

    // Nombre de usuario para la conexión a la base de datos. 
    // Este es el usuario con permisos para acceder y realizar operaciones en la base de datos.
    private static final String USER = "sa";

    // Contraseña asociada con el usuario de la base de datos. 
    // Se utiliza para autenticar la conexión a la base de datos.
    private static final String PASSWORD = "Lab2med";

    // Método que obtiene una conexión a la base de datos utilizando la URL, el usuario y la contraseña definidos.
    // Este método arroja una excepción SQLException si ocurre un problema al establecer la conexión.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
