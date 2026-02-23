package com.knowvault.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * TestConnection - Prueba de conexión a la base de datos
 *
 * @author Kevin García Gutiérrez
 */
public class TestConnection {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("KNOWVAULT - TEST DE CONEXION A BASE DE DATOS");
        System.out.println("==================================================");
        System.out.println();

        try {
            // 1. Obtener conexión
            System.out.println("1. Intentando conectar a MySQL...");
            Connection conn = DatabaseConnection.getConnection();
            System.out.println();

            // 2. Verificar que está conectado
            System.out.println("2. Verificando conexion...");
            if (DatabaseConnection.isConnected()) {
                System.out.println("OK - Conexion activa");
            } else {
                System.out.println("ERROR - Conexion no activa");
                return;
            }
            System.out.println();

            // 3. Probar consulta simple
            System.out.println("3. Ejecutando consulta de prueba...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DATABASE() as db_name, VERSION() as db_version");

            if (rs.next()) {
                String dbName = rs.getString("db_name");
                String dbVersion = rs.getString("db_version");
                System.out.println("   Base de datos: " + dbName);
                System.out.println("   Version MySQL: " + dbVersion);
            }

            rs.close();
            stmt.close();
            System.out.println();

            // 4. Contar registros de prueba
            System.out.println("4. Contando registros en tablas...");
            Statement countStmt = conn.createStatement();

            String[] tables = {"users", "categories", "documents", "document_chunks",
                    "tags", "query_history"};

            for (String table : tables) {
                ResultSet countRs = countStmt.executeQuery("SELECT COUNT(*) as total FROM " + table);
                if (countRs.next()) {
                    int total = countRs.getInt("total");
                    System.out.println("   " + table + " : " + total + " registros");
                }
                countRs.close();
            }

            countStmt.close();
            System.out.println();

            // 5. Cerrar conexión
            System.out.println("5. Cerrando conexion...");
            DatabaseConnection.closeConnection();
            System.out.println();

            System.out.println("==================================================");
            System.out.println("TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println();
            System.err.println("==================================================");
            System.err.println("ERROR EN LA PRUEBA");
            System.err.println("==================================================");
            System.err.println("Tipo: " + e.getClass().getSimpleName());
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println();
            System.err.println("Posibles causas:");
            System.err.println("1. MySQL no esta corriendo (abre XAMPP y inicia MySQL)");
            System.err.println("2. Base de datos 'knowvault' no existe");
            System.err.println("3. MySQL Connector no esta en el classpath");
            System.err.println("4. Credenciales incorrectas");
            e.printStackTrace();
        }
    }
}