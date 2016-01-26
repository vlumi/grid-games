/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.game;

import java.util.Arrays;

/**
 * Game variant.
 *
 * Each variant has a distinct rule set, which defines the size of the field and
 * has methods for determining the game-ending condition.
 *
 * @author vlumi
 */
public enum GameVariant {
    /**
     * Free-style Gomoku game, i.e. five-in-a-row.
     */
    GOMOKU("gomoku", new MnkGameRuleSet(19, 19, 5)),
    /**
     * Tic-tac-toe.
     */
    TICTACTOE("tictactoe", new MnkGameRuleSet(3, 3, 3)),
    UNKNOWN("", null);

    private GameVariant(String value, GameRuleSet ruleSet) {
        this.value = value;
        this.ruleSet = ruleSet;
    }

    private final String value;
    private final GameRuleSet ruleSet;

    public String getValue() {
        return value;
    }

    public GameRuleSet getRuleSet() {
        return ruleSet;
    }

    public int getColumns() {
        return ruleSet.getColumns();
    }

    public int getRows() {
        return ruleSet.getRows();
    }

    public static GameVariant ofValue(String value) {
        return Arrays.asList(GameVariant.values())
                .stream()
                .filter(entry -> entry.getValue().equals(value))
                .findAny()
                .orElse(UNKNOWN);

    }

}
