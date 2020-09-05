package com.bs.epic.battleships.util;

import com.bs.epic.battleships.events.ErrorEvent;

import java.util.Optional;

public class Result {
    private Optional<ErrorEvent> error = Optional.empty();
    public boolean success;

    public Result(boolean success, ErrorEvent error) {
        this.success = success;
        if (error != null) this.error = Optional.of(error);
    }

    public ErrorEvent getError() {
        return error.get();
    }
}
