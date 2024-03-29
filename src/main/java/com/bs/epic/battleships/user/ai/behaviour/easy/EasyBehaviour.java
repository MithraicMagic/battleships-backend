package com.bs.epic.battleships.user.ai.behaviour.easy;

import com.bs.epic.battleships.game.grid.GridPos;
import com.bs.epic.battleships.lobby.Lobby;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.user.ai.behaviour.BaseBehaviour;

import java.util.ArrayList;
import java.util.List;

public class EasyBehaviour extends BaseBehaviour {

    public EasyBehaviour(int delay, List<AiMessage> responses) {
        super(delay, responses);
    }

    @Override
    public void onYourTurn(Lobby lobby, String uid, ArrayList<GridPos> shotPositions) {
        var t = getTaskThread(() -> {
            var pos = GridPos.random();
            while (shotPositions.contains(pos)) {
                pos = GridPos.random();
            }

            shotPositions.add(pos);
            var result = lobby.shoot(uid, pos);
            if (!result.success) {
                System.out.println("AI tried to shoot a cell twice");
            }
        });
        t.start();
    }
}
