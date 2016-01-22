/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import fi.misaki.gomoku.server.game.GameSide;
import fi.misaki.gomoku.server.game.GameBoardPosition;
import fi.misaki.gomoku.server.game.GameBoardChecker;
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
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GameSide side;

        column = 0;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 0;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 1;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[3];
        winningPositions[0] = new GameBoardPosition(2, 0, side);
        winningPositions[1] = new GameBoardPosition(2, 1, side);
        winningPositions[2] = new GameBoardPosition(2, 2, side);
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 3x3x3, horizontal win.
     */
    public void testGame_3x3x3_win_horizontal() {
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GameSide side;

        column = 0;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[3];
        winningPositions[0] = new GameBoardPosition(0, 0, side);
        winningPositions[1] = new GameBoardPosition(1, 0, side);
        winningPositions[2] = new GameBoardPosition(2, 0, side);
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 3x3x3, diagonal right win.
     */
    public void testGame_3x3x3_win_diagonalRight() {
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GameSide side;

        column = 2;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 0;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[3];
        winningPositions[0] = new GameBoardPosition(0, 2, side);
        winningPositions[1] = new GameBoardPosition(1, 1, side);
        winningPositions[2] = new GameBoardPosition(2, 0, side);
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 3x3x3, diagonal left win.
     */
    public void testGame_3x3x3_win_diagonalLeft() {
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GameSide side;

        column = 0;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[3];
        winningPositions[0] = new GameBoardPosition(0, 0, side);
        winningPositions[1] = new GameBoardPosition(1, 1, side);
        winningPositions[2] = new GameBoardPosition(2, 2, side);
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 3x3x3, tie.
     */
    public void testGame_3x3x3_tie() {
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int winningLength = 3;
        int column;
        int row;
        GameSide side;

        column = 0;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 1;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 0;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 1;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 1;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 1;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 0;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(false, GameBoardChecker.isWinnable(grid, winningLength));

        column = 2;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(false, GameBoardChecker.isWinnable(grid, winningLength));
    }

    /**
     * Test case: 19x19x5, diagonal right win.
     */
    public void testGame_19x19x5_win() {
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[19][19];
        int winningLength = 5;
        int column;
        int row;
        GameSide side;

        column = 5;
        row = 5;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 6;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 5;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 5;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 6;
        row = 4;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 8;
        row = 6;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 3;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 7;
        row = 6;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 4;
        row = 6;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 8;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));

        column = 8;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[5];
        winningPositions[0] = new GameBoardPosition(4, 6, side);
        winningPositions[1] = new GameBoardPosition(5, 5, side);
        winningPositions[2] = new GameBoardPosition(6, 4, side);
        winningPositions[3] = new GameBoardPosition(7, 3, side);
        winningPositions[4] = new GameBoardPosition(8, 2, side);
        Assert.assertArrayEquals(winningPositions,
                GameBoardChecker.getWinningPositions(grid, winningLength, column, row).toArray());
        assertEquals(true, GameBoardChecker.isWinnable(grid, winningLength));
    }
}
