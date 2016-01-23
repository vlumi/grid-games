/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.game;

import java.io.Serializable;
import java.util.Objects;

/**
 * A non-empty position on the game board.
 *
 * @author vlumi
 */
public class GameBoardPosition implements Serializable {

    private static final long serialVersionUID = 1493691960987953985L;

    /**
     * The column of the position.
     */
    private final int column;
    /**
     * The row of the position.
     */
    private final int row;
    /**
     * The side occupying the position.
     */
    private final GameSide side;

    /**
     * Standard constructor.
     *
     * @param column The column of the position.
     * @param row The row of the position.
     * @param side The side occupying the position.
     */
    public GameBoardPosition(int column, int row, GameSide side) {
        this.column = column;
        this.row = row;
        this.side = side;
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
     * For comparison and sorting.
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

    /**
     * For comparison and sorting.
     *
     * @return
     */
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
