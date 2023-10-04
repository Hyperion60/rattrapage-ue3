package org.example.server;

import org.example.account.Account;
import org.example.db.Database;

import java.sql.Connection;

public class Parser {
    public String setParser(Connection connection, ServerThread serverThread, Globals globals, String line) {
        String category = line.split(" ")[0];

        if (category.isEmpty()) {
            return "error: no category found!";
        }

        if (line.equals("ping")) {
            return "pong";
        }

        switch (category) {
            case "login":
                Account.login(connection, serverThread, line);
                if (serverThread.user == null) {
                    return "error: invalid credentials";
                }
                return Integer.toString(serverThread.user.id);
            case "logout":
                serverThread.user = null;
                return "OK";
            case "signup":
                String ans = Account.createAccount(connection, serverThread, globals, line);
                Account.login(connection, serverThread, line.replace("signup ", "login "));
                return ans;

        }

        if (line.startsWith("msg ")) {
            // Check if user is connected
            if (serverThread.user == null) {
                return "error: user is not connected!";
            }
            // Add general message
            String msg = line.split("msg ")[1];
            Database.addGeneralMessage(connection, msg, serverThread.user.id);
            return "OK";
        } else if (line.startsWith("msg-")) {
            // Add private message
            int receiveId = Integer.parseInt(line.split(" ")[0].split("msg-")[1]);
            String msg = line.split("msg-"+receiveId)[1];
            Database.addPrivateMessage(connection, msg, serverThread.user.id, receiveId);
            return "OK";
        } else {
            return "error: unknown command!";
        }
    }

    public String getParser(Connection connection, ServerThread serverThread, Globals globals, String line) {
        String category = line.split(" ")[0];

        if (category.startsWith("get-")) {
            int userId = Integer.parseInt(line.split(" ")[0].split("-")[1]);
            return Database.getPrivateMessage(connection, globals, serverThread.user.id, userId);
        } else if (category.startsWith("get")) {
            return Database.getGeneralMessage(connection, globals);
        } else {
            return setParser(connection, serverThread, globals, line);
        }
    }
}
