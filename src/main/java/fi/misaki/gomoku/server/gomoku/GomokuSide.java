/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import java.util.Arrays;

/**
 * Gomoku player side.
 *
 * @author vlumi
 */
public enum GomokuSide {
    WHITE(1),
    BLACK(2),
    UNKNOWN(-1);

    private GomokuSide(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public GomokuSide getOther() {
        switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                return UNKNOWN;
        }
    }

    public static GomokuSide ofValue(int value) {
        return Arrays.asList(GomokuSide.values())
                .stream()
                .filter(entry -> entry.getValue() == value)
                .findAny()
                .orElse(UNKNOWN);

    }

}
