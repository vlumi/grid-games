/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.game;

import java.util.Arrays;

/**
 * Gomoku player side.
 *
 * @author vlumi
 */
public enum GameSide {
    WHITE(1),
    BLACK(2),
    UNKNOWN(-1);

    private GameSide(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public GameSide getOther() {
        switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                return UNKNOWN;
        }
    }

    public static GameSide ofValue(int value) {
        return Arrays.asList(GameSide.values())
                .stream()
                .filter(entry -> entry.getValue() == value)
                .findAny()
                .orElse(UNKNOWN);

    }

}
