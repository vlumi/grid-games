/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.game;

import java.util.Arrays;

/**
 * State of a cell on the game board.
 *
 * @author vlumi
 */
public enum BoardCellState {
    FREE(0),
    WHITE(GameSide.WHITE),
    BLACK(GameSide.BLACK),
    UNKNOWN(-1);

    private BoardCellState(int value) {
        this.value = value;
    }

    private BoardCellState(GameSide side) {
        this.value = side.getValue();
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static BoardCellState ofValue(int value) {
        return Arrays.asList(BoardCellState.values())
                .stream()
                .filter(entry -> entry.getValue() == value)
                .findAny()
                .orElse(UNKNOWN);

    }

}
