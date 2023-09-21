package org.example.db;

import org.example.account.User;
import org.example.server.Globals;

import java.sql.*;

public class Database {
    public static Connection newConnection() {
        Connection connection = null;
        try {
             connection = DriverManager.getConnection(
                    "jdbc:mariadb://127.0.0.1:58366/mydatabase",
                    "myuser",
                    "secret"
            );
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return connection;
    }

    public static void initListUser(Connection connection, Globals globals) {
        try (
                PreparedStatement statement = connection.prepareStatement("""
                SELECT * FROM ACCOUNT
            """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            int id;
            String user, password;
            while (resultSet.next()) {
                id = resultSet.getInt(1);
                user = resultSet.getString(2);
                password = resultSet.getString(3);
                globals.listUser.add(new User(id, user, password));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void addGeneralMessage(Connection connection, String message, int user) {
        try (
            PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO GENERAL(user_id, message) VALUES (?, ?)
            """)
        ) {
            statement.setInt(1, user);
            statement.setString(2, message);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void addPrivateMessage(Connection connection, String message, int sender, int receiver) {
        try (
                PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO PRIVATEMSG(sender, receiver, message) VALUES (?, ?, ?)
            """)
        ) {
            statement.setInt(1, sender);
            statement.setInt(2, receiver);
            statement.setString(3, message);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public static String getGeneralMessage(Connection connection, Globals globals) {
        try (
            PreparedStatement statement = connection.prepareStatement("""
                SELECT user_id, message FROM GENERAL ORDER BY id DESC
            """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            String result = "";
            for (int i = 0; i < 20; i++) {
                resultSet.next();
                result = String.format("%s\t%s %s", result, globals.getUsername(resultSet.getInt(1)), resultSet.getString(2));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return "";
    }

    public static String getPrivateMessage(Connection connection, Globals globals, int localId, int remoteId) {
        try (
                PreparedStatement statement = connection.prepareStatement("""
                SELECT sender, message FROM PRIVATEMSG WHERE (sender='?' AND receiver='?') OR (sender='?' AND receiver='?') ORDER BY id DESC
            """)
        ) {
            statement.setInt(1, localId);
            statement.setInt(2, remoteId);
            statement.setInt(3, remoteId);
            statement.setInt(4, localId);
            ResultSet resultSet = statement.executeQuery();
            String result = "";
            for (int i = 0; i < 20; i++) {
                resultSet.next();
                result = String.format("%s\t%s %s", result, globals.getUsername(resultSet.getInt(1)), resultSet.getString(2));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return "";
    }
}
