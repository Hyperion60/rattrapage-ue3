package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

import org.example.account.Account;
import org.example.db.Database;
import org.example.server.Globals;
import org.example.server.ServerThread;

public class Main {
    private static void init_db(Connection connection, Globals globals) {
        // Add tables
        try (
                PreparedStatement statement = connection.prepareStatement("""
                    create table if not exists ACCOUNT
                      (
                          id       int auto_increment
                              primary key,
                          login    varchar(255) not null,
                          password varchar(255) not null,
                          constraint login
                              unique (login)
                      );
            """)
        )
        {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (
                PreparedStatement statement = connection.prepareStatement("""
                    create table if not exists GENERAL
                      (
                          id      int auto_increment
                              primary key,
                          user_id int           not null,
                          message varchar(1024) not null,
                          constraint GENERAL_ACCOUNT_id_fk
                              foreign key (user_id) references ACCOUNT (id)
                      );
            """)
        )
        {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (
                PreparedStatement statement = connection.prepareStatement("""
                    create table if not exists PRIVATEMSG
                      (
                          id       int auto_increment,
                          user1_id int           not null,
                          user2_id int           not null,
                          message  varchar(1024) not null,
                          constraint PRIVATEMSG_pk
                              primary key (id)
                      );
            """)
        )
        {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Create admin account
        try (
                PreparedStatement statement = connection.prepareStatement("""
                    create table if not exists PRIVATEMSG
                      (
                          id       int auto_increment,
                          user1_id int           not null,
                          user2_id int           not null,
                          message  varchar(1024) not null,
                          constraint PRIVATEMSG_pk
                              primary key (id)
                      );
            """)
        )
        {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        Account.initCreateAccount(connection, globals, "admin", "pass");
    }
    public static void main(String[] args) throws SQLException {
        // Initialization
        System.out.print("Server initialization...");
        Globals lists = new Globals();
        Connection connection = Database.newConnection();
        init_db(connection, lists);
        connection.close();
        System.out.println("Done");
        System.out.println("Waiting clients...");

        // Server thread
        ServerSocket server = null;

        try {
            server = new ServerSocket(42233);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

        Socket new_client = null;
        while (true) {
            try {
                new_client = server.accept();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }

            System.out.println("Client accepted");

            if (new_client != null) {
                ServerThread client = new ServerThread(new_client, lists, connection);
                lists.listClient.add(client);
                Thread t = new Thread(client);
                t.start();
                client.setThread(t);
                System.out.println("Thread launched");
                new_client = null;
            }
        }
    }
}