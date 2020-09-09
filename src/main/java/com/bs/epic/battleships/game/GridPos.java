package com.bs.epic.battleships.game;

import com.bs.epic.battleships.util.Util;

import java.util.Objects;

public class GridPos {
    public int i, j;

    public GridPos(int i, int j) {
        this.i = i;
        this.j = j;
    }

    static public GridPos random() {
        return new GridPos(Util.randomInt(0, 9), Util.randomInt(0, 9));
    }

    public int index(int size) {
        return i + j * size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridPos gridPos = (GridPos) o;
        return i == gridPos.i &&
                j == gridPos.j;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }
}
