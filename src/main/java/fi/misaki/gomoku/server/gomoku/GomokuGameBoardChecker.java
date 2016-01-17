/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

/**
 *
 * @author vlumi
 */
public class GomokuGameBoardChecker {

    public static boolean isWinningPosition(int[][] grid, int winningLength, int column, int row, GomokuSide side) {
        int sideValue = side.getValue();
        switch (side) {
            case WHITE:
            case BLACK:
                if (checkHorizontal(grid, winningLength, sideValue, column, row)
                        || checkVertical(grid, winningLength, sideValue, column, row)
                        || checkDiagonalRight(grid, winningLength, sideValue, column, row)
                        || checkDiagonalLeft(grid, winningLength, sideValue, column, row)) {
                    return true;
                }
                break;
        }
        return false;
    }

    private static boolean checkHorizontal(int[][] grid, int winningLength, int sideValue, int column, int row) {
        int length = 1;
        for (int x = column - 1; x >= 0 && grid[x][row] == sideValue; x--) {
            length++;
        }
        for (int x = column + 1; x < grid.length && grid[x][row] == sideValue; x++) {
            length++;
        }
        return length >= winningLength;
    }

    private static boolean checkVertical(int[][] grid, int winningLength, int sideValue, int column, int row) {
        int length = 1;
        for (int y = row - 1; y >= 0 && grid[column][y] == sideValue; y--) {
            length++;
        }
        for (int y = row + 1; y < grid.length && grid[column][y] == sideValue; y++) {
            length++;
        }
        return length >= winningLength;
    }

    private static boolean checkDiagonalRight(int[][] grid, int winningLength, int sideValue, int column, int row) {
        int length = 1;
        for (int x = column - 1, y = row + 1; x >= 0 && y < grid.length && grid[x][y] == sideValue; x--, y++) {
            length++;
        }
        for (int x = column + 1, y = row - 1; x < grid.length && y >= 0 && grid[x][y] == sideValue; x++, y--) {
            length++;
        }
        return length >= winningLength;
    }

    private static boolean checkDiagonalLeft(int[][] grid, int winningLength, int sideValue, int column, int row) {
        int length = 1;
        for (int x = column - 1, y = row - 1; x >= 0 && y >= 0 && grid[x][y] == sideValue; x--, y--) {
            length++;
        }
        for (int x = column + 1, y = row + 1; x < grid.length && y < grid.length && grid[x][y] == sideValue; x++, y++) {
            length++;
        }
        return length >= winningLength;
    }

    /**
     * Check if the game is still in a winnable state.
     *
     * The checking is done for all tiles, left-to-right and top-to-bottom.
     *
     * @param grid
     * @param winningLength
     * @return
     */
    public static boolean isWinnable(int[][] grid, int winningLength) {
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid.length; column++) {
                int sideValue = grid[column][row];
                switch (GomokuCellState.ofValue(sideValue)) {
                    case FREE:
                        if (checkIfWinnableFromPosition(grid, winningLength, GomokuSide.WHITE.getValue(), column, row)
                                || checkIfWinnableFromPosition(grid, winningLength, GomokuSide.BLACK.getValue(), column, row)) {
                            return true;
                        }
                    case WHITE:
                    case BLACK:
                        if (checkIfWinnableFromPosition(grid, winningLength, sideValue, column, row)) {
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
    private static boolean checkIfWinnableFromPosition(int[][] grid, int winningLength, int sideValue, int column, int row) {
        boolean checkHorizontal = column <= grid.length - winningLength;
        boolean checkVertical = row <= grid.length - winningLength;
        boolean checkDiagonalRight = checkHorizontal && checkVertical;
        boolean checkDiagonalLeft = column >= 4 && checkVertical;

        return checkHorizontal && checkHorizontalPotential(grid, winningLength, sideValue, column, row)
                || checkVertical && checkVerticalPotential(grid, winningLength, sideValue, column, row)
                || checkDiagonalRight && checkDiagonalRightPotential(grid, winningLength, sideValue, column, row)
                || checkDiagonalLeft && checkDiagonalLeftPotential(grid, winningLength, sideValue, column, row);
    }

    private static boolean checkHorizontalPotential(int[][] grid, int winningLength, int sideValue, int column, int row) {
        for (int i = 0; i < winningLength; i++) {
            if (!isSideOrFree(grid[column + i][row], sideValue)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkVerticalPotential(int[][] grid, int winningLength, int sideValue, int column, int row) {
        for (int i = 0; i < winningLength; i++) {
            if (!isSideOrFree(grid[column][row + i], sideValue)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkDiagonalRightPotential(int[][] grid, int winningLength, int sideValue, int column, int row) {
        for (int i = 0; i < winningLength; i++) {
            if (!isSideOrFree(grid[column + i][row + i], sideValue)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkDiagonalLeftPotential(int[][] grid, int winningLength, int sideValue, int column, int row) {
        for (int i = 0; i < winningLength; i++) {
            if (!isSideOrFree(grid[column - i][row + i], sideValue)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSideOrFree(int side, int comparedSide) {
        return side == comparedSide || side == GomokuCellState.FREE.getValue();
    }

}
