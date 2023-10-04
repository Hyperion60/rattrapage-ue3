package org.example.account;

import org.example.db.Database;
import org.example.server.Globals;
import org.example.server.ServerThread;

import java.sql.*;

import java.util.Objects;

public class Account {
    public static void initCreateAccount(Connection connection, Globals globals, String name, String pass) {
        try {
            connection.close();
        } catch (SQLException f) {}
        connection = Database.newConnection();

        // Verify if account already exist
        try (
                PreparedStatement statement = connection.prepareStatement("""
                    SELECT login FROM ACCOUNT
            """)
        )
        {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (Objects.equals(name, resultSet.getString(1))) {
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

        // Adding new account
        try (
                PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO ACCOUNT (login, password) VALUES (?, ?);
            """)
        )
        {
            statement.setString(1, name);
            statement.setString(2, pass);
            statement.execute();
            Database.initListUser(connection, globals);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public static String createAccount(Connection connection, ServerThread serverThread, Globals globals, String line) {
        try {
            connection.close();
        } catch (SQLException f) {}
        connection = Database.newConnection();

        String name = line.split(" ")[1];
        String pass = line.split(" ")[2];
        // Verify if account already exist
        try (
                PreparedStatement statement = connection.prepareStatement("""
                    SELECT login FROM ACCOUNT
            """)
        )
        {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (Objects.equals(name, resultSet.getString(1))) {
                    return "error: Username already taken";
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Adding new account
        try (
                PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO ACCOUNT (login, password) VALUES (?, ?);
            """)
        )
        {
            statement.setString(1, name);
            statement.setString(2, pass);
            statement.execute();
            // Database.initListUser(connection, globals);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

        // Get id
        int id = Database.getId(name);
        globals.listUser.add(new User(id, name, pass));
        return Integer.toString(id);
    }

    public static void login(Connection connection, ServerThread serverThread, String line) {
        String name = line.replace("login ", "").split(" ")[0];
        String passwd = line.replace("login ", "").split(" ")[1];

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM ACCOUNT");
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException f) {}
            connection = Database.newConnection();
        }

        try {
            statement = connection.prepareStatement("SELECT * FROM ACCOUNT");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (Objects.equals(name, resultSet.getString(2)) && Objects.equals(passwd, resultSet.getString(3))) {
                    serverThread.user = new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

    }
}
