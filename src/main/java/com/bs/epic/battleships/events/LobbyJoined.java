package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class LobbyJoined {
    @Doc(description = "The unique id for the lobby")
    public int id;
    @Doc(description = "Opponent's name")
    public String otherName;
    @Doc(description = "Boolean stating if the player is the lobby's leader")
    public boolean leader;

    public LobbyJoined(int id, String otherName, boolean leader) {
        this.id = id;
        this.otherName = otherName;
        this.leader = leader;
    }
}
