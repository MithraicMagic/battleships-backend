package com.bs.epic.battleships.user;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users;

    public UserManager() {
        users = new ArrayList<>();
    }

    public void add(User user) {
        System.out.println("Adding user with uid: " + user.uid);
        users.add(user);
    }

    public void replaceUserByPlayer(Player p) {
        System.out.println("Replacing user with uid: " + p.uid + " with Player");
        User user = null;
        for (var u : users) if (u.socket == p.socket) user = u;

        if (user != null) {
            users.remove(user);
            users.add(p);
        }
    }

    public void remove(User user) {
        System.out.println("Removing user with uid: " + user.uid);
        users.remove(user);
    }

    public boolean nameExists(String name) {
        for (var u : users) {
            if (u.type == UserType.User) continue;

            var p = (Player) u;
            if (p.name.equals(name)) return true;
        }
        return false;
    }

    public User get(String uid) {
        if (uid == null) return null;

        for (var user : users) {
            if (user.isEqual(uid)) return user;
        }
        return null;
    }

    public Player getPlayer(String uid) {
        for (var user : users) {
            if (user.isEqual(uid)) return (Player) user;
        }
        return null;
    }

    public Player getByCode(String code) {
        for (var u : users) {
            if (u.type == UserType.User) continue;

            var p = (Player) u;
            if (p.state == UserState.Available && p.code.equals(code)) return p;
        }
        return null;
    }

    public User getBySocket(SocketIOClient socket) {
        for (var user : users) if (user.socket == socket) return user;
        return null;
    }
}