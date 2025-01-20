package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                System.out.println("Подключение к базе данных успешно!");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());//
        }
    }
}
