package com.bs.epic.battleships.lobby;

import com.bs.epic.battleships.events.LobbyJoined;
import com.bs.epic.battleships.events.ReconnectToLobby;
import com.bs.epic.battleships.game.Game;
import com.bs.epic.battleships.game.GameState;
import com.bs.epic.battleships.game.GridPos;
import com.bs.epic.battleships.user.Player;
import com.bs.epic.battleships.user.PlayerMessage;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.user.UserType;
import com.bs.epic.battleships.util.result.Error;
import com.bs.epic.battleships.util.result.Result;
import com.bs.epic.battleships.util.result.Success;

import java.util.ArrayDeque;
import java.util.Collection;

public class Lobby {
    public int id;
    public Game game;

    public Player playerOne;
    public Player playerTwo;

    private ArrayDeque<PlayerMessage> messages;

    public Lobby(int id, Player playerOne, Player playerTwo) {
        this.id = id;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;

        messages = new ArrayDeque<>();

        playerOne.leader = true;

        playerOne.setState(UserState.Lobby);
        playerTwo.setState(UserState.Lobby);
    }

    public void initGame(int size) {
        game = new Game(size);
        game.init(playerOne, playerTwo);
    }

    public void clearPlayers() {
        playerOne.onLobbyRemoved();
        playerTwo.onLobbyRemoved();
    }

    public Result shoot(String uid, GridPos pos) {
        var res = game.shoot(getPlayer(uid), getOtherPlayer(uid), pos);
        if (res.success) {
            if (!game.checkVictory()) switchTurn();
        }
        return res;
    }

    public void switchTurn() {
        if (playerOne.state == UserState.YourTurn) {
            playerOne.setState(UserState.OpponentTurn);
            playerTwo.setState(UserState.YourTurn);
        }
        else {
            playerOne.setState(UserState.YourTurn);
            playerTwo.setState(UserState.OpponentTurn);
        }
    }

    public Result donePlacing(String uid) {
        var player = getPlayer(uid);
        var result = game.donePlacing(player);
        if (result.success) {
            player.setState(UserState.SetupComplete);

            if (playerOne.state == UserState.SetupComplete && playerTwo.state == UserState.SetupComplete) {
                game.state = GameState.InGame;
                playerOne.setState(UserState.YourTurn);
                playerTwo.setState(UserState.OpponentTurn);
                sendEventToLobby("gameStarted");
            }

            var otherPlayer = getOtherPlayer(player);
            otherPlayer.socket.sendEvent("opponentSubmitted");
        }
        return result;
    }

    public boolean hasPlayer(String uid) { return getPlayer(uid) != null; }

    public Player getPlayer(String uid) {
        return playerOne.isEqual(uid) ? playerOne : playerTwo;
    }
    public Player getOtherPlayer(String uid) { return playerOne.isEqual(uid) ? playerTwo : playerOne; }
    public Player getOtherPlayer(Player p) { return playerOne.isEqual(p) ? playerTwo : playerOne; }

    public void sendEventToLobby(String event) {
        if (playerOne != null) playerOne.socket.sendEvent(event);
        if (playerTwo != null) playerTwo.socket.sendEvent(event);
    }

    public void sendLobbyJoinedEvent() {
        if (playerOne != null) playerOne.socket.sendEvent("lobbyJoined", new LobbyJoined(id, playerTwo.name, true));
        if (playerTwo != null) playerTwo.socket.sendEvent("lobbyJoined", new LobbyJoined(id, playerOne.name, false));
    }

    public Result sendMessage(String message, Player sender) {
        var receiver = getOtherPlayer(sender);

        if (message.length() < 3 || message.length() > 100) {
            return new Error("sendMessage", "Message should be between 3 and 100 characters");
        }

        messages.addLast(new PlayerMessage(message, sender.name, receiver.name));
        if (messages.size() > 20) messages.removeFirst();

        receiver.socket.sendEvent("messageReceived", messages.getLast());
        return new Success();
    }

    public void onPlayerDisconnect(Player p) {
        var other = getOtherPlayer(p);
        other.setState(UserState.OpponentReconnecting);
    }

    public void onPlayerReconnect(Player p) {
        var other = getOtherPlayer(p);
        other.revertState();
        other.socket.sendEvent("opponentReconnected");
        p.socket.sendEvent("reconnectLobby", new ReconnectToLobby(p.name, other.name, p.leader, id));
    }

    public void onPlayerLeave(Player p) {
        var other = getOtherPlayer(p);
        other.socket.sendEvent("opponentLeft");
        other.setState(UserState.Available);
    }

    public void onRematchRequest(Player player) {
        player.setState(UserState.Rematch);
        getOtherPlayer(player).socket.sendEvent("otherRematch");

        if (playerOne.state == UserState.Rematch && playerTwo.state == UserState.Rematch) {
            playerOne.onLobbyRemoved();
            playerTwo.onLobbyRemoved();

            initGame(10);
            sendEventToLobby("setupStarted");
        }
    }

    public Collection<PlayerMessage> getMessages() {
        return messages;
    }
}
