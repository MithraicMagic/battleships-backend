package com.bs.epic.battleships.game;

import com.bs.epic.battleships.user.Player;
import com.bs.epic.battleships.user.UserState;
import com.bs.epic.battleships.util.result.Error;
import com.bs.epic.battleships.util.result.ShootSuccess;
import com.bs.epic.battleships.util.result.Result;
import com.bs.epic.battleships.util.result.Success;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Game {
    private int size;
    public GameState state;

    private Player one, two;

    private Map<String, Ship> ships = new HashMap<>() {{
        put("carrier", new Ship("carrier", 5));
        put("battleship", new Ship("battleship", 4));
        put("cruiser", new Ship("cruiser", 3));
        put("submarine", new Ship("submarine", 3));
        put("minesweeper", new Ship("minesweeper", 2));
    }};

    public Game(int size) {
        this.size = size;
    }

    public void init(Player one, Player two) {
        one.cells = new ArrayList<>();
        two.cells = new ArrayList<>();

        one.ships = new HashMap<>();
        two.ships = new HashMap<>();

        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                one.cells.add(new GridCell());
                two.cells.add(new GridCell());
            }
        }

        one.setState(UserState.Setup);
        two.setState(UserState.Setup);

        this.one = one;
        this.two = two;
    }

    public Result shoot(Player p, int i, int j) {
        if (!inBounds(i, j)) {
            return new Error("shoot", "You can't shoot outside the grid");
        }

        var cell = p.cells.get(coordsToIndex(i, j));
        switch (cell.state) {
            case HitShip:
            case HitWater:
                return new Error("shoot", "You've already shot this cell");
            case Water:
                return new ShootSuccess(false, false);
            default:
                cell.ship.hitPieces++;
                return new ShootSuccess(true, cell.ship.isDestroyed());
        }
    }

    public Result placeShip(Player p, String s, int i, int j, boolean horizontal) {
        if (!ships.containsKey(s)) return new Error("placeShip", "This ship doesn't exist");

        removeShip(s, p);
        if (i < 0 || i > size || j < 0 || j > size) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        var templateShip = ships.get(s);
        var ship = new Ship(templateShip, i, j, horizontal);
        var delta = ship.length / 2;

        if (horizontal) return this.placeShipHorizontal(p, ship, i, j, delta);

        return this.placeShipVertical(p, ship, i, j, delta);
    }

    private Result placeShipHorizontal(Player p, Ship ship, int i, int j, int delta) {
        var left = i - getMinDelta(ship, delta);
        var right = i + delta;

        if (!inBounds(left, right)) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        for (var index = left; index <= right; index++) {
            if (p.cells.get(coordsToIndex(index, j)).state != CellState.Water) {
                return new Error("placeShip", "The ship doesn't fit there");
            }
        }

        for (var index = left; index <= right; index++) {
            p.cells.get(coordsToIndex(index, j)).state = CellState.Ship;
            p.cells.get(coordsToIndex(index, j)).ship = ship;
        }

        p.ships.put(ship.name, ship);
        return new Success();
    }

    private Result placeShipVertical(Player p, Ship ship, int i, int j, int delta) {
        var top = j - getMinDelta(ship, delta);
        var bottom = j + delta;

        if (!inBounds(top, bottom)) {
            return new Error("placeShip", "You can't place the ship outside of your grid");
        }

        for (var index = top; index <= bottom; index++) {
            if (p.cells.get(coordsToIndex(i, index)).state != CellState.Water) {
                return new Error("placeShip", "The ship doesn't fit there");
            }
        }

        for (var index = top; index <= bottom; index++) {
            p.cells.get(coordsToIndex(i, index)).state = CellState.Ship;
            p.cells.get(coordsToIndex(i, index)).ship = ship;
        }

        p.ships.put(ship.name, ship);
        return new Success();
    }

    public void removeShip(String s, Player p) {
        p.ships.remove(s);
        for (var cell : p.cells) {
            if (cell.ship != null && cell.ship.name.equals(s)) {
                cell.ship = null;
                cell.state = CellState.Water;
            }
        }
    }

    public void checkVictory() {
        if (checkVictory(one.getShips())) {
            one.setState(UserState.GameWon);
            two.setState(UserState.GameLost);
        }
        else if (checkVictory(two.getShips())) {
            one.setState(UserState.GameLost);
            two.setState(UserState.GameWon);
        }
    }

    private boolean checkVictory(Collection<Ship> ships) {
        boolean allDestroyed = true;
        for (var ship : ships) {
            if (!ship.isDestroyed()) allDestroyed = false;
        }

        return allDestroyed;
    }

    public Result donePlacing(Player p) {
        if (p.ships.size() != ships.size()) {
            return new Error("donePlacing", "You have not yet placed all your ships");
        }

        p.donePlacing = true;
        return new Success();
    }

    private int coordsToIndex(int i, int j) {
        return i + j * size;
    }

    private boolean inBounds(int min, int max) {
        return min >= 0 && min < size && max > 0 && max < size;
    }

    private int getMinDelta(Ship ship, int delta) {
        return (ship.length % 2 == 0 ? (delta / 2) : delta);
    }
}
