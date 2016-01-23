/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule set for the M,N,K-game
 *
 * @author vlumi
 */
public class MnkGameRuleSet extends GameRuleSet {

    private final int winningLength;

    /**
     *
     * @param columns
     * @param rows
     * @param winningLength
     */
    public MnkGameRuleSet(int columns, int rows, int winningLength) {
        super(columns, rows);
        this.winningLength = winningLength;
    }

    @Override
    public boolean placePiece(int[][] grid, int column, int row, GameSide side) {
        if (!isMoveValid(grid, column, row, side)) {
            return false;
        }
        grid[column][row] = side.getValue();
        return true;
    }

    @Override
    public boolean isMoveValid(int[][] grid, int column, int row, GameSide side) {
        BoardCellState oldValue = BoardCellState.ofValue(grid[column][row]);
        return oldValue == BoardCellState.FREE;
    }

    /**
     * Checks if the current position is within part of a winning sequence.
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
    @Override
    public List<GameBoardPosition> getWinningPositions(int[][] grid, int column, int row) {
        int sideValue = grid[column][row];
        switch (GameSide.ofValue(sideValue)) {
            case WHITE:
            case BLACK:
                if (checkHorizontal(grid, sideValue, column, row)) {
                    return getHorizontalWinningPositions(grid, sideValue, column, row);
                } else if (checkVertical(grid, sideValue, column, row)) {
                    return getVerticalWinningPositions(grid, sideValue, column, row);
                } else if (checkDiagonalRight(grid, sideValue, column, row)) {
                    return getDiagonalRightWinningPositions(grid, sideValue, column, row);
                } else if (checkDiagonalLeft(grid, sideValue, column, row)) {
                    return getDiagonalLeftWinningPositions(grid, sideValue, column, row);
                }
                break;
        }
        return new ArrayList<>();
    }

    private boolean checkHorizontal(int[][] grid, int sideValue, int column, int row) {
        int length = 1;
        for (int x = column - 1; x >= 0 && grid[x][row] == sideValue; x--) {
            length++;
        }
        for (int x = column + 1; x < grid.length && grid[x][row] == sideValue; x++) {
            length++;
        }
        return length >= this.winningLength;
    }

    private List<GameBoardPosition> getHorizontalWinningPositions(int[][] grid, int sideValue, int column, int row) {
        List<GameBoardPosition> winningPositions = new ArrayList<>();
        int x = column;
        while (x > 0 && grid[x - 1][row] == sideValue) {
            x--;
        }
        while (x < grid.length && grid[x][row] == sideValue) {
            winningPositions.add(new GameBoardPosition(x, row, GameSide.ofValue(sideValue)));
            x++;
        }

        return winningPositions;
    }

    private boolean checkVertical(int[][] grid, int sideValue, int column, int row) {
        int length = 1;
        for (int y = row - 1; y >= 0 && grid[column][y] == sideValue; y--) {
            length++;
        }
        for (int y = row + 1; y < grid.length && grid[column][y] == sideValue; y++) {
            length++;
        }
        return length >= this.winningLength;
    }

    private List<GameBoardPosition> getVerticalWinningPositions(int[][] grid, int sideValue, int column, int row) {
        List<GameBoardPosition> winningPositions = new ArrayList<>();
        int y = row;
        while (y > 0 && grid[column][y - 1] == sideValue) {
            y--;
        }
        while (y < grid.length && grid[column][y] == sideValue) {
            winningPositions.add(new GameBoardPosition(column, y, GameSide.ofValue(sideValue)));
            y++;
        }

        return winningPositions;
    }

    private boolean checkDiagonalRight(int[][] grid, int sideValue, int column, int row) {
        int length = 1;
        for (int x = column - 1, y = row + 1; x >= 0 && y < grid.length && grid[x][y] == sideValue; x--, y++) {
            length++;
        }
        for (int x = column + 1, y = row - 1; x < grid.length && y >= 0 && grid[x][y] == sideValue; x++, y--) {
            length++;
        }
        return length >= this.winningLength;
    }

    private List<GameBoardPosition> getDiagonalRightWinningPositions(int[][] grid, int sideValue, int column, int row) {
        List<GameBoardPosition> winningPositions = new ArrayList<>();
        int x = column, y = row;
        while (x > 0 && y < grid.length && grid[x - 1][y + 1] == sideValue) {
            x--;
            y++;
        }
        while (x < grid.length && y >= 0 && grid[x][y] == sideValue) {
            winningPositions.add(new GameBoardPosition(x, y, GameSide.ofValue(sideValue)));
            x++;
            y--;
        }

        return winningPositions;
    }

    private boolean checkDiagonalLeft(int[][] grid, int sideValue, int column, int row) {
        int length = 1;
        for (int x = column - 1, y = row - 1; x >= 0 && y >= 0 && grid[x][y] == sideValue; x--, y--) {
            length++;
        }
        for (int x = column + 1, y = row + 1; x < grid.length && y < grid.length && grid[x][y] == sideValue; x++, y++) {
            length++;
        }
        return length >= this.winningLength;
    }

    private List<GameBoardPosition> getDiagonalLeftWinningPositions(int[][] grid, int sideValue, int column, int row) {
        List<GameBoardPosition> winningPositions = new ArrayList<>();
        int x = column, y = row;
        while (x > 0 && y > 0 && grid[x - 1][y - 1] == sideValue) {
            x--;
            y--;
        }
        while (x < grid.length && y < grid.length && grid[x][y] == sideValue) {
            winningPositions.add(new GameBoardPosition(x, y, GameSide.ofValue(sideValue)));
            x++;
            y++;
        }

        return winningPositions;
    }

    /**
     * Check if the game is still in a winnable state.
     *
     * The checking is done for all tiles, left-to-right and top-to-bottom.
     *
     * @param grid
     * @return
     */
    @Override
    public boolean isWinnable(int[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid.length; column++) {
                int sideValue = grid[column][row];
                switch (BoardCellState.ofValue(sideValue)) {
                    case FREE:
                        if (checkIfWinnableFromPosition(grid, GameSide.WHITE.getValue(), column, row)
                                || checkIfWinnableFromPosition(grid, GameSide.BLACK.getValue(), column, row)) {
                            return true;
                        }
                    case WHITE:
                    case BLACK:
                        if (checkIfWinnableFromPosition(grid, sideValue, column, row)) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param sideValue
     * @param column
     * @param row
     * @return
     */
    private boolean checkIfWinnableFromPosition(int[][] grid, int sideValue, int column, int row) {
        boolean checkHorizontal = column <= grid.length - this.winningLength;
        boolean checkVertical = row <= grid.length - this.winningLength;
        boolean checkDiagonalRight = checkHorizontal && checkVertical;
        boolean checkDiagonalLeft = column >= 4 && checkVertical;

        return checkHorizontal && checkHorizontalPotential(grid, sideValue, column, row)
                || checkVertical && checkVerticalPotential(grid, sideValue, column, row)
                || checkDiagonalRight && checkDiagonalRightPotential(grid, sideValue, column, row)
                || checkDiagonalLeft && checkDiagonalLeftPotential(grid, sideValue, column, row);
    }

    private boolean checkHorizontalPotential(int[][] grid, int sideValue, int column, int row) {
        for (int i = 0; i < this.winningLength; i++) {
            if (!isSideOrFree(grid[column + i][row], sideValue)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkVerticalPotential(int[][] grid, int sideValue, int column, int row) {
        for (int i = 0; i < this.winningLength; i++) {
            if (!isSideOrFree(grid[column][row + i], sideValue)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDiagonalRightPotential(int[][] grid, int sideValue, int column, int row) {
        for (int i = 0; i < this.winningLength; i++) {
            if (!isSideOrFree(grid[column + i][row + i], sideValue)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDiagonalLeftPotential(int[][] grid, int sideValue, int column, int row) {
        for (int i = 0; i < this.winningLength; i++) {
            if (!isSideOrFree(grid[column - i][row + i], sideValue)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSideOrFree(int side, int comparedSide) {
        return side == comparedSide || side == BoardCellState.FREE.getValue();
    }

}
