/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author vlumi
 */
public class GomokuGameBoard implements Serializable {

    private static final long serialVersionUID = 2774766165752029443L;

    private static final int DEFAULT_SIDE_LENGTH = 19;
    private static final int DEFAULT_WINNING_LENGTH = 5;

    private final int sideLength;
    private final int winningLength;
    private final int[][] grid;
    private int turnsLeft;
    private final List<GomokuGamePosition> winningPositions = Collections.synchronizedList(new ArrayList<>());

    public GomokuGameBoard() {
        this(DEFAULT_SIDE_LENGTH, DEFAULT_WINNING_LENGTH);
    }

    public GomokuGameBoard(int sideLength, int winningLength) {
        this.sideLength = sideLength;
        this.winningLength = winningLength;
        this.grid = new int[sideLength][sideLength];
        reset();
        this.turnsLeft = this.sideLength * this.sideLength;
    }

    public void reset() {
        for (int[] row : this.grid) {
            Arrays.fill(row, GomokuCellState.FREE.getValue());
        }
    }

    public GomokuCellState getSideAtPosition(int column, int row) {
        synchronized (this.grid) {
            return GomokuCellState.ofValue(this.grid[column][row]);
        }
    }

    public boolean placeWhitePiece(int column, int row) {
        return this.placePiece(GomokuSide.WHITE, column, row);
    }

    public boolean placeBlackPiece(int column, int row) {
        return this.placePiece(GomokuSide.BLACK, column, row);
    }

    private boolean placePiece(GomokuSide side, int column, int row) {
        synchronized (this.grid) {
            if (!isCellFree(column, row)) {
                return false;
            }
            this.grid[column][row] = side.getValue();
            this.turnsLeft--;
            return true;
        }
    }

    private boolean isCellFree(int column, int row) {
        GomokuCellState oldValue = GomokuCellState.ofValue(this.grid[column][row]);
        return oldValue == GomokuCellState.FREE;
    }

    /**
     * Check if placing the turn results in a win.
     *
     * @param turn
     * @return
     */
    public boolean isWinningTurn(GomokuGamePosition turn) {
        if (turn == null) {
            return false;
        }
        int row = turn.getRow();
        int column = turn.getColumn();

        synchronized (this.grid) {
            List<GomokuGamePosition> positions
                    = GomokuGameBoardChecker.getWinningPositions(this.grid, this.winningLength, column, row);
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
    public List<GomokuGamePosition> getWinningPositions() {
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
            return GomokuGameBoardChecker.isWinnable(this.grid, this.winningLength);
        }
    }

}
