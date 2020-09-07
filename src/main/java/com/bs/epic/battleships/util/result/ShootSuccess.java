package com.bs.epic.battleships.util.result;

import com.bs.epic.battleships.game.GridPos;
import com.bs.epic.battleships.util.result.Result;
import com.bs.epic.battleships.util.result.ShootResult;

public class ShootSuccess extends Result {
    public ShootResult result;

    public ShootSuccess(boolean hitShip, boolean destroyedShip, GridPos pos) {
        super(true, null);
        this.result = new ShootResult(hitShip, destroyedShip, pos);
    }
}
