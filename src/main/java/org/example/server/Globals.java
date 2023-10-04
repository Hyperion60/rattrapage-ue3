package org.example.server;

import org.example.account.User;

import java.util.ArrayList;
import java.util.List;
public class Globals {
    public List<ServerThread> listClient;

    public List<User> listUser;

    public Globals() {
        this.listClient = new ArrayList<>();
        this.listUser = new ArrayList<>();
    }

    public String getUsername(int id) {
        for (User u : this.listUser) {
            if (u.id == id) {
                return u.name;
            }
        }
        return "";
    }

    public void addUsername(int id, String name) {
        this.listUser.add(new User(id, name, ""));
    }
}
