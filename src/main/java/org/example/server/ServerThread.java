package org.example.server;

import org.example.account.User;

import java.net.Socket;
import java.sql.Connection;

public class ServerThread implements Runnable {
    private final Socket client;

    public User user;

    private Thread thread;

    public IOCommandes stream;

    private Globals globals;
    private Parser parser;
    private Connection connection;

    public ServerThread(Socket client, Globals globals, Connection connection) {
        this.client = client;
        this.globals = globals;
        this.parser = new Parser();
        this.connection = connection;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        String line;
        stream = new IOCommandes(this.client);
        while (true) {
            line = stream.lireReseau();
            System.out.println("client> " + line);
            if (line == null) {
                this.globals.listClient.remove(this);
                System.out.println("Client disconnected");
                break;
            }

            if (line.isEmpty()) {
                this.stream.ecrireReseau("Error: Unknown command");
                continue;
            }

            // Parser line
            String response = this.parser.getParser(this.connection, this, globals, line);
            if (response.equals("OK SET")) {
                this.stream.ecrireReseau("ALL OK");
            } else {
                this.stream.ecrireReseau(response);
            }
        }
    }
}
