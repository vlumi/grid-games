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
public class GomokuGameTurn {

    private final int column;
    private final int row;
    private final GomokuSide side;

    public GomokuGameTurn(int column, int row, GomokuSide value) {
        this.column = column;
        this.row = row;
        this.side = value;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public GomokuSide getSide() {
        return side;
    }

}
