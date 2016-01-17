/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import java.util.Arrays;

/**
 * State of a cell on the gomoku game field.
 *
 * @author vlumi
 */
public enum GomokuCellState {
    FREE(0),
    WHITE(GomokuSide.WHITE),
    BLACK(GomokuSide.BLACK),
    UNKNOWN(-1);

    private GomokuCellState(int value) {
        this.value = value;
    }

    private GomokuCellState(GomokuSide side) {
        this.value = side.getValue();
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static GomokuCellState ofValue(int value) {
        return Arrays.asList(GomokuCellState.values())
                .stream()
                .filter(entry -> entry.getValue() == value)
                .findAny()
                .orElse(UNKNOWN);

    }

}
