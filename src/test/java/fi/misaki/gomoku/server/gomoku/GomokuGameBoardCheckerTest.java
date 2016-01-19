/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vlumi
 */
public class GomokuGameBoardCheckerTest {

    /**
     * Test game winning logic of GomokuGameBoardChecker.
     */
    @Test
    public void testGame() {
        this.testGame_3x3x3_win_vertical();
        this.testGame_3x3x3_win_horizontal();
        this.testGame_3x3x3_win_diagonalLeft();
        this.testGame_3x3x3_win_diagonalRight();
        this.testGame_3x3x3_tie();
        this.testGame_19x19x5_win();
    }

    /**
     * Test case: 3x3x3, vertical win.
     */
    public void testGame_3x3x3_win_vertical() {
        GomokuGamePosition[] winningPositions = new GomokuGamePosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GomokuSide side;

        column = 0;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 0;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 1;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GomokuGamePosition[3];
        winningPositions[0] = new GomokuGamePosition(2, 0, side);
        winningPositions[1] = new GomokuGamePosition(2, 1, side);
        winningPositions[2] = new GomokuGamePosition(2, 2, side);
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 3x3x3, horizontal win.
     */
    public void testGame_3x3x3_win_horizontal() {
        GomokuGamePosition[] winningPositions = new GomokuGamePosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GomokuSide side;

        column = 0;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GomokuGamePosition[3];
        winningPositions[0] = new GomokuGamePosition(0, 0, side);
        winningPositions[1] = new GomokuGamePosition(1, 0, side);
        winningPositions[2] = new GomokuGamePosition(2, 0, side);
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 3x3x3, diagonal right win.
     */
    public void testGame_3x3x3_win_diagonalRight() {
        GomokuGamePosition[] winningPositions = new GomokuGamePosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GomokuSide side;

        column = 2;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 0;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GomokuGamePosition[3];
        winningPositions[0] = new GomokuGamePosition(0, 2, side);
        winningPositions[1] = new GomokuGamePosition(1, 1, side);
        winningPositions[2] = new GomokuGamePosition(2, 0, side);
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 3x3x3, diagonal left win.
     */
    public void testGame_3x3x3_win_diagonalLeft() {
        GomokuGamePosition[] winningPositions = new GomokuGamePosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GomokuSide side;

        column = 0;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GomokuGamePosition[3];
        winningPositions[0] = new GomokuGamePosition(0, 0, side);
        winningPositions[1] = new GomokuGamePosition(1, 1, side);
        winningPositions[2] = new GomokuGamePosition(2, 2, side);
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 3x3x3, tie.
     */
    public void testGame_3x3x3_tie() {
        GomokuGamePosition[] winningPositions = new GomokuGamePosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GomokuSide side;

        column = 0;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 0;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 1;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 1;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(false, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(false, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 19x19x5, diagonal right win.
     */
    public void testGame_19x19x5_win() {
        GomokuGamePosition[] winningPositions = new GomokuGamePosition[0];
        int[][] grid = new int[19][19];
        int winningLength = 5;
        int column;
        int row;
        GomokuSide side;

        column = 5;
        row = 5;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 6;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 5;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 5;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 4;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 8;
        row = 6;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 3;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 6;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 4;
        row = 6;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 8;
        row = 2;
        side = GomokuSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));

        column = 8;
        row = 2;
        side = GomokuSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GomokuGamePosition[5];
        winningPositions[0] = new GomokuGamePosition(4, 6, side);
        winningPositions[1] = new GomokuGamePosition(5, 5, side);
        winningPositions[2] = new GomokuGamePosition(6, 4, side);
        winningPositions[3] = new GomokuGamePosition(7, 3, side);
        winningPositions[4] = new GomokuGamePosition(8, 2, side);
        Assert.assertArrayEquals(winningPositions,
                GomokuGameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GomokuGameBoardChecker.isWinnable(grid, winningLength));
    }
}
