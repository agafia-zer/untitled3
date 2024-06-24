package org.example;

import com.mysql.cj.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/sbb";
    private static final String USER = "root";
    private static final String PASSWORD = "agafia2003";

    // Метод для установления соединения с базой данных
    public static Connection getConnection() {
        try {
            DriverManager.registerDriver(new Driver());
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось подключиться к базе данных", e);
        }
    }

}
