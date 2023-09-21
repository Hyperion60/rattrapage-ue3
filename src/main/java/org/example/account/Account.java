package org.example.account;

import org.example.db.Database;
import org.example.server.Globals;
import org.example.server.ServerThread;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Account {
    public static void createAccount(Connection connection, Globals globals, String name, String pass) {
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
            Database.initListUser(connection, globals);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void login(Connection connection, ServerThread serverThread, String line) {
        String name = line.replace("login ", "").split(" ")[0];
        String passwd = line.replace("login ", "").split(" ")[1];
        try (
                PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM ACCOUNT
            """)
        )
        {
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
