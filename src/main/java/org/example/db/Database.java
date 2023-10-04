package org.example.db;

import org.example.account.User;
import org.example.server.Globals;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.Objects;

public class Database {
    public static Connection newConnection() {
        Connection connection = null;
        try {
             connection = DriverManager.getConnection(
                    "jdbc:mariadb://127.0.0.1:33006/rattrapage",
                    "root",
                    "password"
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
        try {
            connection.close();
        } catch (SQLException f) {}
        connection = Database.newConnection();

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
        try {
            connection.close();
        } catch (SQLException f) {}
        connection = Database.newConnection();

        try (
        PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO PRIVATEMSG(user1_id, user2_id, message) VALUES (?, ?, ?)
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
        try {
            connection.close();
        } catch (SQLException f) {}
        connection = Database.newConnection();

        try (
            PreparedStatement statement = connection.prepareStatement("""
                SELECT user_id, message FROM GENERAL ORDER BY id DESC
            """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            String result = "";
            int i = 0;
            while (resultSet.next()) {
                if (i > 20) {
                    break;
                }
                int id = resultSet.getInt(1);
                String name = globals.getUsername(id);
                if (Objects.equals(name, "")) {
                    name = Database.getUsername(id);
                    globals.addUsername(id, name);
                }

                result = String.format("%s\t%s %s", result, name, resultSet.getString(2));
                i += 1;
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return "";
    }

    public static String getPrivateMessage(Connection connection, Globals globals, int localId, int remoteId) {
        try {
            connection.close();
        } catch (SQLException f) {}
        connection = Database.newConnection();

        try (
                PreparedStatement statement = connection.prepareStatement("""
                SELECT user1_id, user2_id, message FROM PRIVATEMSG ORDER BY id DESC
            """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            String result = "";

            int i = 0;
            while (resultSet.next()) {
                if (i > 20) {
                    break;
                }
                int user1_id = resultSet.getInt(1);
                int user2_id = resultSet.getInt(2);
                String message = resultSet.getString(3);
                if ((user1_id == localId && user2_id == remoteId) || (user1_id == remoteId && user2_id == localId)) {
                    String name = globals.getUsername(user1_id);
                    if (Objects.equals(name, "")) {
                        name = Database.getUsername(user1_id);
                        globals.addUsername(user1_id, name);
                    }
                    result = String.format("%s\t%s %s", result, name, message);
                }
                i += 1;
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return "";
    }

    public static String getUsername(int id) {
        Connection connection = Database.newConnection();

        String name = "";
        try (
                PreparedStatement statement = connection.prepareStatement("""
                SELECT id, login FROM ACCOUNT
            """)
        ) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt(1) == id) {
                    name = resultSet.getString(2);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return name;
    }

    public static int getId(String login) {
        Connection connection = Database.newConnection();

        int id = 0;
        try (
                PreparedStatement statement = connection.prepareStatement("""
                SELECT id, login FROM ACCOUNT
            """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (Objects.equals(resultSet.getString(2), login)) {
                    id = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return id;
    }
}
