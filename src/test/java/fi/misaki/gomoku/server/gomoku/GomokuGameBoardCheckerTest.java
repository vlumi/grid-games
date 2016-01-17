/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vlumi
 */
public class GomokuGameBoardCheckerTest {

    /**
     * Test of isWinningPosition method, of class GomokuGameBoardChecker.
     */
    @Test
    public void testGame_3x3x3_win() {
        System.out.println("isWinningPosition");
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GomokuSide side;

        column = 0;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 0;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 1;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(true, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test of class GomokuGameBoardChecker.
     */
    @Test
    public void testGame_3x3x3_tie() {
        System.out.println("isWinningPosition");
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GomokuSide side;

        column = 0;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 0;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 1;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 1;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(false, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(false, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test of isWinningPosition method, of class GomokuGameBoardChecker.
     */
    @Test
    public void testGame_19x19x5_win() {
        System.out.println("isWinningPosition");
        int[][] grid = new int[19][19];
        int winningLength = 5;
        int column;
        int row;
        GomokuSide side;

        column = 5;
        row = 5;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 6;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 5;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 5;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 4;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 8;
        row = 6;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 3;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 6;
        side = GomokuSide.BLACK;
         grid[column][row] = side.getValue();
       assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 4;
        row = 6;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 8;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        assertEquals(false, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 3;
        row = 7;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        assertEquals(true, GomokuGameBoardChecker.isWinningPosition(grid, winningLength, column, row, side));
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }
}
