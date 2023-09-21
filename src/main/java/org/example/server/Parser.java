package org.example.server;

import org.example.account.Account;
import org.example.db.Database;

import java.sql.Connection;

public class Parser {
    public void setParser(Connection connection, ServerThread serverThread, Globals globals, String line) {
        String category = line.split(" ")[0];

        if (category.isEmpty()) {
            serverThread.stream.ecrireReseau("error: no category found!");
            return;
        }

        switch (category) {
            case "login":
                Account.login(connection, serverThread, line);
                return;
            case "logout":
                serverThread.user = null;
                return;
        }

        if (line.startsWith("msg ")) {
            // Check if user is connected
            if (serverThread.user == null) {
                serverThread.stream.ecrireReseau("error: user is not connected!");
                return;
            }
            // Add general message
            String msg = line.split("msg ")[1];
            Database.addGeneralMessage(connection, msg, serverThread.user.id);
        } else if (line.startsWith("msg-")) {
            // Add private message
            int receiveId = Integer.parseInt(line.split(" ")[0].split("msg-")[1]);
            String msg = line.split("msg-"+receiveId)[1];
            Database.addPrivateMessage(connection, msg, serverThread.user.id, receiveId);
        } else {
            serverThread.stream.ecrireReseau("error: unknown command!");
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
            setParser(connection, serverThread, globals, line);
            return "OK SET";
        }
    }
}
