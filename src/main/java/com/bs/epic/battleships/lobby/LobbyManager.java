package com.bs.epic.battleships;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.ArrayList;

public class LobbyManager {
    private ArrayList<Lobby> lobbies;

    public LobbyManager() {
        lobbies = new ArrayList<>();
    }

    public Lobby getLobby(int lobbyId) {
        for (var l : lobbies) if (l.id == lobbyId) return l;
        return null;
    }

    public void add(Lobby lobby) {
        lobbies.add(lobby);
    }

    public void remove(Lobby lobby) {
        lobbies.remove(lobby);
    }

    public Lobby getLobbyBySocket(SocketIOClient s) {
        for (var l : lobbies) if (l.playerOne == s || l.playerTwo == s) return l;
        return null;
    }

    public Lobby getLobbyByUid(String uid) {
        for (var l : lobbies) if (l.playerOne.isEqual(uid) || l.playerTwo.isEqual(uid)) return l;
        return null;
    }
}