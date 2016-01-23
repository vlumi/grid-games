/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.game;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vlumi
 */
public class MnkGameRuleSetTest {

    /**
     * Test game winning logic of MnkGameRuleSet.
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
        GameRuleSet instance = new MnkGameRuleSet(3, 3, 3);
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int column;
        int row;
        GameSide side;

        column = 0;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 1;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 0;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 0;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 1;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[3];
        winningPositions[0] = new GameBoardPosition(2, 0, side);
        winningPositions[1] = new GameBoardPosition(2, 1, side);
        winningPositions[2] = new GameBoardPosition(2, 2, side);
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));
    }

    /**
     * Test case: 3x3x3, horizontal win.
     */
    public void testGame_3x3x3_win_horizontal() {
        GameRuleSet instance = new MnkGameRuleSet(3, 3, 3);
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int column;
        int row;
        GameSide side;

        column = 0;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 1;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 0;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[3];
        winningPositions[0] = new GameBoardPosition(0, 0, side);
        winningPositions[1] = new GameBoardPosition(1, 0, side);
        winningPositions[2] = new GameBoardPosition(2, 0, side);
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));
    }

    /**
     * Test case: 3x3x3, diagonal right win.
     */
    public void testGame_3x3x3_win_diagonalRight() {
        GameRuleSet instance = new MnkGameRuleSet(3, 3, 3);
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int column;
        int row;
        GameSide side;

        column = 2;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 0;
        row = 0;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 0;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 1;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[3];
        winningPositions[0] = new GameBoardPosition(0, 2, side);
        winningPositions[1] = new GameBoardPosition(1, 1, side);
        winningPositions[2] = new GameBoardPosition(2, 0, side);
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));
    }

    /**
     * Test case: 3x3x3, diagonal left win.
     */
    public void testGame_3x3x3_win_diagonalLeft() {
        GameRuleSet instance = new MnkGameRuleSet(3, 3, 3);
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int column;
        int row;
        GameSide side;

        column = 0;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 0;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 0;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 1;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        winningPositions = new GameBoardPosition[3];
        winningPositions[0] = new GameBoardPosition(0, 0, side);
        winningPositions[1] = new GameBoardPosition(1, 1, side);
        winningPositions[2] = new GameBoardPosition(2, 2, side);
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));
    }

    /**
     * Test case: 3x3x3, tie.
     */
    public void testGame_3x3x3_tie() {
        GameRuleSet instance = new MnkGameRuleSet(3, 3, 3);
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[3][3];
        int column;
        int row;
        GameSide side;

        column = 0;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 1;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 0;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 0;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 1;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 2;
        row = 1;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 0;
        row = 1;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 0;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(false, instance.isWinnable(grid));

        column = 2;
        row = 2;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(false, instance.isWinnable(grid));
    }

    /**
     * Test case: 19x19x5, diagonal right win.
     */
    public void testGame_19x19x5_win() {
        GameRuleSet instance = new MnkGameRuleSet(19, 19, 5);
        GameBoardPosition[] winningPositions = new GameBoardPosition[0];
        int[][] grid = new int[19][19];
        int column;
        int row;
        GameSide side;

        column = 5;
        row = 5;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 6;
        row = 6;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 7;
        row = 5;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 6;
        row = 5;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 6;
        row = 4;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 8;
        row = 6;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 7;
        row = 3;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 7;
        row = 6;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 4;
        row = 6;
        side = GameSide.WHITE;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

        column = 8;
        row = 2;
        side = GameSide.BLACK;
        grid[column][row] = side.getValue();
        Assert.assertArrayEquals(winningPositions,
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));

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
                instance.getWinningPositions(grid, column, row).toArray());
        assertEquals(true, instance.isWinnable(grid));
    }
}
