/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.game;

import java.util.List;

/**
 * Rule set type for a grid game type.
 *
 * Methods for validating moves and determining game-ending conditions are to be
 * determined here, called by the game framework on user actions.
 *
 * @author vlumi
 */
public abstract class GameRuleSet {

    private final int columns;
    private final int rows;

    public GameRuleSet(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
    }

    /**
     * Attempt to place the piece in the given position on the grid.
     *
     * @param grid
     * @param column
     * @param row
     * @param side
     * @return
     */
    abstract public boolean placePiece(int[][] grid, int column, int row, GameSide side);

    /**
     * Check if the given move would be valid on the given grid.
     *
     * @param grid
     * @param column
     * @param row
     * @param side
     * @return
     */
    abstract public boolean isMoveValid(int[][] grid, int column, int row, GameSide side);

    /**
     * Check if the current position is within part of a winning sequence.
     *
     * If yes, returns all the positions of the winning sequence, sorted by
     * column and row.
     *
     * If now, returns an empty list.
     *
     * @param grid The grid to check.
     * @param column Column of the position to check.
     * @param row Row of the position to check.
     * @return List of positions of the winning sequence, or an empty list if
     * not a winning sequence.
     */
    abstract public List<GameBoardPosition> getWinningPositions(int[][] grid, int column, int row);

    /**
     * Check if the game is still in a winnable state.
     *
     * The checking is done for all tiles, left-to-right and top-to-bottom.
     *
     * @param grid
     * @return
     */
    abstract public boolean isWinnable(int[][] grid);

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

}
