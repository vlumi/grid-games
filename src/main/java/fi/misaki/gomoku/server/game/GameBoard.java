/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author vlumi
 */
public class GameBoard implements Serializable {

    private static final long serialVersionUID = 2774766165752029443L;

    private static final int DEFAULT_COLUMNS = 19;
    private static final int DEFAULT_ROWS = 19;
    private static final int DEFAULT_WINNING_LENGTH = 5;

    private final int columns;
    private final int rows;
    private final int winningLength;
    private final int[][] grid;
    private int turnsLeft;
    private final List<GameBoardPosition> winningPositions = Collections.synchronizedList(new ArrayList<>());

    public GameBoard() {
        this(DEFAULT_COLUMNS, DEFAULT_ROWS, DEFAULT_WINNING_LENGTH);
    }

    public GameBoard(int columns, int rows, int winningLength) {
        this.columns = columns;
        this.rows = rows;
        this.winningLength = winningLength;
        this.grid = new int[columns][rows];
        reset();
    }

    public void reset() {
        for (int[] row : this.grid) {
            Arrays.fill(row, BoardCellState.FREE.getValue());
        }
        winningPositions.clear();
        this.turnsLeft = this.columns * this.rows;
    }

    public BoardCellState getSideAtPosition(int column, int row) {
        synchronized (this.grid) {
            return BoardCellState.ofValue(this.grid[column][row]);
        }
    }

    public boolean placeWhitePiece(int column, int row) {
        return this.placePiece(GameSide.WHITE, column, row);
    }

    public boolean placeBlackPiece(int column, int row) {
        return this.placePiece(GameSide.BLACK, column, row);
    }

    private boolean placePiece(GameSide side, int column, int row) {
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
        BoardCellState oldValue = BoardCellState.ofValue(this.grid[column][row]);
        return oldValue == BoardCellState.FREE;
    }

    /**
     * Check if placing the turn results in a win.
     *
     * @param turn
     * @return
     */
    public boolean isWinningTurn(GameBoardPosition turn) {
        System.out.println("isWinningTurn");
        dump();
        if (turn == null) {
            return false;
        }
        int row = turn.getRow();
        int column = turn.getColumn();

        synchronized (this.grid) {
            List<GameBoardPosition> positions
                    = GameBoardChecker.getWinningPositions(this.grid, this.winningLength, column, row);
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
        System.out.println("isWinnable");
        dump();
        if (this.turnsLeft <= 0) {
            return false;
        }
        synchronized (this.grid) {
            return GameBoardChecker.isWinnable(this.grid, this.winningLength);
        }
    }

    private void dump() {
        for (int[] row : grid) {
            StringBuffer rowStr = new StringBuffer();
            for (int cell : row) {
                rowStr.append(cell);
            }
            System.out.println(rowStr);
        }
    }

}
