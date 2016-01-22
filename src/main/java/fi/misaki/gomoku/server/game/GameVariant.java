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
public enum GameVariant {
    GOMOKU("gomoku", 19, 19, 5),
    TICTACTOE("tictactoe", 3, 3, 3),
    UNKNOWN("", 0, 0, 0);

    private GameVariant(String value, int columns, int rows, int winLength) {
        this.value = value;
        this.columns = columns;
        this.rows = rows;
        this.winLength = winLength;
    }

    private final String value;
    private final int columns;
    private final int rows;
    private final int winLength;

    public String getValue() {
        return value;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getWinLength() {
        return winLength;
    }

    public static GameVariant ofValue(String value) {
        return Arrays.asList(GameVariant.values())
                .stream()
                .filter(entry -> entry.getValue().equals(value))
                .findAny()
                .orElse(UNKNOWN);

    }

}
