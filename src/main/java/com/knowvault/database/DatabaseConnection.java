package com.knowvault.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton para conexión a MySQL
 *
 * Maneja la conexión a la base de datos KnowVault
 * usando el patrón Singleton para reutilizar la misma conexión.
 *
 * @author Kevin García Gutiérrez
 * @version 1.0
 * @since 2026-02-23
 */
public class DatabaseConnection {

    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/knowvault";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP por defecto no tiene password

    // Instancia única (Singleton)
    private static Connection connection = null;

    /**
     * Constructor privado para evitar instanciación externa
     */
    private DatabaseConnection() {
        // Constructor privado (Singleton pattern)
    }

    /**
     * Obtiene la conexión a la base de datos
     * Si no existe, la crea. Si existe, la reutiliza.
     *
     * @return Connection objeto de conexión a MySQL
     * @throws SQLException si hay error de conexión
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establecer la conexión
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                System.out.println("Conexión a base de datos establecida exitosamente");

            } catch (ClassNotFoundException e) {
                System.err.println("Error: Driver de MySQL no encontrado");
                System.err.println("Asegúrate de que mysql-connector-j.jar está en el classpath");
                throw new SQLException("Driver de MySQL no encontrado", e);
            } catch (SQLException e) {
                System.err.println("Error de conexión a la base de datos:");
                System.err.println("   URL: " + URL);
                System.err.println("   Usuario: " + USER);
                System.err.println("   Mensaje: " + e.getMessage());
                throw e;
            }
        }

        return connection;
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión a base de datos cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica si la conexión está activa
     *
     * @return true si la conexión está activa, false si no
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}