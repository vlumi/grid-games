/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.game;

import java.util.Objects;

/**
 *
 * @author vlumi
 */
public class GameBoardPosition {

    private final int column;
    private final int row;
    private final GameSide side;

    public GameBoardPosition(int column, int row, GameSide value) {
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

    public GameSide getSide() {
        return side;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameBoardPosition)) {
            return super.equals(obj);
        }
        GameBoardPosition otherSide = (GameBoardPosition) obj;
        return this.column == otherSide.getColumn()
                && this.row == otherSide.getRow()
                && this.side == otherSide.getSide();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.column;
        hash = 79 * hash + this.row;
        hash = 79 * hash + Objects.hashCode(this.side);
        return hash;
    }

    @Override
    public String toString() {
        return "GameBoardPosition{" + "column=" + column + ", row=" + row + ", side=" + side + '}';
    }

}
