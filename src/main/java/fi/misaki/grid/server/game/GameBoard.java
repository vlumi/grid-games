/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The game board tracking the status of each position.
 *
 * Wraps the rule set of the given variant, keeping track of the game state,
 * validating moves and detecting game-ending conditions.
 *
 * @author vlumi
 */
public class GameBoard implements Serializable {

    private static final long serialVersionUID = 2774766165752029443L;

    /**
     * Number of columns on the board.
     */
    private final int columns;
    /**
     * Number of rows on the board
     */
    private final int rows;
    /**
     * The rule set to use, for validating moves and tracking game-ending
     * conditions.
     */
    private final GameRuleSet ruleSet;
    /**
     * The game board, state of each position. The value is the same as for the
     * BoardCellState enum.
     */
    private final int[][] grid;
    /**
     * How many empty positions are on the board.
     */
    private int turnsLeft;
    /**
     * When a game-ending condition has been met, the winning positions on the
     * board.
     */
    private final List<GameBoardPosition> winningPositions = Collections.synchronizedList(new ArrayList<>());

    /**
     * Default constructor.
     *
     * @param variant
     */
    public GameBoard(GameVariant variant) {
        this.columns = variant.getColumns();
        this.rows = variant.getRows();
        this.ruleSet = variant.getRuleSet();
        this.grid = new int[columns][rows];
        initialize();
    }

    /**
     * Reset the board to initial state.
     */
    public void reset() {
        initialize();
    }

    /**
     * Initialize the board state.
     */
    private void initialize() {
        for (int[] row : this.grid) {
            Arrays.fill(row, BoardCellState.FREE.getValue());
        }
        winningPositions.clear();
        this.turnsLeft = this.columns * this.rows;
    }

    /**
     * Returns the state of the given position.
     *
     * @param column
     * @param row
     * @return
     */
    public BoardCellState getSideAtPosition(int column, int row) {
        synchronized (this.grid) {
            return BoardCellState.ofValue(this.grid[column][row]);
        }
    }

    /**
     * Attempt to place a white piece in the given position.
     *
     * @param column
     * @param row
     * @return
     */
    public boolean placeWhitePiece(int column, int row) {
        return this.placePiece(GameSide.WHITE, column, row);
    }

    /**
     * Attempt to place a black piece in the given position.
     *
     * @param column
     * @param row
     * @return
     */
    public boolean placeBlackPiece(int column, int row) {
        return this.placePiece(GameSide.BLACK, column, row);
    }

    /**
     * Attempt to place a piece in the given position.
     *
     * @param side
     * @param column
     * @param row
     * @return
     */
    private boolean placePiece(GameSide side, int column, int row) {
        synchronized (this.grid) {
            if (this.ruleSet.placePiece(grid, column, row, side)) {
                this.turnsLeft--;
                return true;
            }
            return false;
        }
    }

    /**
     * Check if placing the turn results in a win.
     *
     * @param turn
     * @return
     */
    public boolean isWinningTurn(GameBoardPosition turn) {
        if (turn == null) {
            return false;
        }
        int row = turn.getRow();
        int column = turn.getColumn();

        synchronized (this.grid) {
            List<GameBoardPosition> positions
                    = this.ruleSet.getWinningPositions(this.grid, column, row);
            if (!positions.isEmpty()) {
                synchronized (this.winningPositions) {
                    this.winningPositions.clear();
                    this.winningPositions.addAll(positions);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public List<GameBoardPosition> getWinningPositions() {
        return new ArrayList<>(this.winningPositions);
    }

    /**
     * Check if the game is still in a winnable state.
     *
     * The checking is done for all tiles, left-to-right and top-to-bottom.
     *
     * @return
     */
    public boolean isWinnable() {
        if (this.turnsLeft <= 0) {
            return false;
        }
        synchronized (this.grid) {
            return this.ruleSet.isWinnable(this.grid);
        }
    }

    @Override
    public String toString() {
        return "GameBoard{" + "columns=" + columns + ", rows=" + rows + ", ruleSet=" + ruleSet + ", grid=" + Arrays.deepToString(grid) + ", turnsLeft=" + turnsLeft + ", winningPositions=" + winningPositions + '}';
    }

}
