package com.bs.epic.battleships.events;

public class PlaceShip {
    public int lobbyId;
    public int i, j;

    public String uid;
    public String ship;

    public boolean horizontal;

    public PlaceShip(int lobbyId, int i, int j, String uid, String ship, boolean horizontal) {
        this.lobbyId = lobbyId;
        this.i = i;
        this.j = j;

        this.uid = uid;
        this.ship = ship;

        this.horizontal = horizontal;
    }
}