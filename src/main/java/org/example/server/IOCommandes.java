package org.example.server;

import java.io.*;
import java.net.Socket;

public class IOCommandes {
    private final BufferedReader lectureEcran;
    private final PrintStream ecritureEcran;
    private final BufferedReader lectureFichier;
    private final PrintStream ecritureFichier;
    private final Socket socket;

    public IOCommandes(Socket socket) {
        InputStreamReader in = new InputStreamReader(System.in);
        this.lectureEcran = new BufferedReader(in);
        this.ecritureEcran = new PrintStream(System.out);
        this.socket = socket;
        this.lectureFichier = null;
        this.ecritureFichier = null;
    }

    public void ecrireEcran(String texte) {
        this.ecritureEcran.println(texte);
    }

    public String lireEcran() throws IOException {
        return this.lectureEcran.readLine();
    }

    public int ecrireReseau(String texte) {
        if (this.socket != null) {
            OutputStream outputStream;
            try {
                outputStream = this.socket.getOutputStream();
            } catch (IOException e) {
                return 1;
            }
            if (outputStream != null) {
                PrintStream out = new PrintStream(outputStream);
                out.println(texte);
                return 0;
            }
        }
        return 1;
    }

    public String lireReseau() {
        String line;
        InputStream inputStream;
        if (this.socket != null) {
            try {
                inputStream = this.socket.getInputStream();
            } catch (IOException exception) {
                inputStream = null;
            }
            if (inputStream != null) {
                InputStreamReader in = new InputStreamReader(inputStream);
                BufferedReader input = new BufferedReader(in);
                try {
                    line = input.readLine();
                } catch (IOException exception) {
                    System.out.println("Echec lecture socket");
                    return null;
                }
                return line;
            }
        }
        return null;
    }

    public void ecrireFichier(String texte) {
        this.ecritureFichier.println(texte);
    }

    public String lireFichier() throws IOException {
        return this.lectureFichier.readLine();
    }
}
