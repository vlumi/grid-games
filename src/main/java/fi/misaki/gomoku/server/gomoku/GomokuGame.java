/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import fi.misaki.gomoku.server.user.User;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author vlumi
 */
public class GomokuGame {

    private final GomokuGameBoard board;

    private final Deque<GomokuGameTurn> turnHistory = new ConcurrentLinkedDeque<>();
    private boolean gameOver = false;

    private User playerWhite;
    private User playerBlack;
    private User winner = null;
    private User loser = null;
    private GomokuSide currentTurn;
    private boolean running = false;

    public GomokuGame() {
        this.board = new GomokuGameBoard();
    }

    public boolean addPlayer(User user) {
        if (this.playerWhite == null) {
            this.playerWhite = user;
            return true;
        }
        if (this.playerBlack == null && this.playerWhite != user) {
            this.playerBlack = user;
            return true;
        }
        return false;
    }

    public void start() {
        synchronized (this) {
            if (this.playerWhite != null
                    && this.playerBlack != null
                    && !this.gameOver) {
                this.currentTurn = GomokuSide.WHITE;
                this.running = true;
            }
            if (this.winner != null && this.loser != null) {
                this.playerWhite = winner;
                this.playerBlack = winner;
            }
        }
    }

    /**
     *
     * @param column
     * @param row
     * @return <code>true</code> if the move was successful.
     */
    public boolean placePiece(int column, int row) {
        if (!this.running) {
            return false;
        }
        boolean turnSuccessful = false;
        User currentPlayer = null;
        User nextPlayer = null;
        switch (this.currentTurn) {
            case WHITE:
                currentPlayer = this.playerWhite;
                nextPlayer = this.playerBlack;
                if (this.board.placeWhitePiece(column, row)) {
                    turnSuccessful = true;
                }
                break;
            case BLACK:
                currentPlayer = this.playerBlack;
                nextPlayer = this.playerWhite;
                if (this.board.placeBlackPiece(column, row)) {
                    turnSuccessful = true;
                }
                break;
        }

        if (turnSuccessful) {
            recordTurn(column, row, this.currentTurn);
            if (this.gameOver) {
                this.winner = currentPlayer;
                this.loser = nextPlayer;
            } else {
                this.currentTurn = this.currentTurn.getOther();
            }
        }
        return turnSuccessful;
    }

    public void leave(User user) {
        // TODO: implement
        terminate();
    }

    public void terminate() {
        this.playerWhite = null;
        this.playerBlack = null;
        this.winner = null;
        this.loser = null;

        this.running = false;
    }

    public boolean isReady() {
        if (this.playerWhite != null
                && this.playerBlack != null
                && !this.gameOver) {
            this.running = true;
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return this.running && !this.gameOver;
    }

    public GomokuSide getCurrentTurn() {
        return this.currentTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public User getWinner() {
        return this.winner;
    }

    public User getLoser() {
        return this.loser;
    }

    public User getPlayerWhite() {
        return this.playerWhite;
    }

    public User getPlayerBlack() {
        return this.playerBlack;
    }

    public User getPlayer(GomokuSide side) {
        switch (side) {
            case WHITE:
                return this.playerWhite;
            case BLACK:
                return this.playerBlack;
        }
        return null;
    }

    public GomokuSide getSide(User player) {
        if (this.playerWhite == player) {
            return GomokuSide.WHITE;
        } else if (this.playerBlack == player) {
            return GomokuSide.BLACK;
        } else {
            return GomokuSide.UNKNOWN;
        }
    }

    public List<GomokuGameTurn> getTurnHistory() {
        synchronized (this.turnHistory) {
            return this.turnHistory.stream().collect(Collectors.toList());
        }
    }

    public GomokuGameTurn getLastTurn() {
        synchronized (this.turnHistory) {
            return this.turnHistory.peekLast();
        }
    }

    public JsonArrayBuilder getTurnHistoryAsJsonArrayBuilder() {
        synchronized (this.turnHistory) {
            JsonArrayBuilder turns = Json.createArrayBuilder();
            this.turnHistory.stream()
                    .map(turn -> Json.createArrayBuilder()
                            .add(turn.getSide().getValue())
                            .add(turn.getColumn())
                            .add(turn.getRow()))
                    .forEachOrdered(turn -> {
                        turns.add(turn);
                    });
            return turns;
        }
    }

    /**
     * Record the move made.
     *
     * @param column
     * @param row
     * @param side
     */
    private void recordTurn(int column, int row, GomokuSide side) {
        GomokuGameTurn move = new GomokuGameTurn(column, row, side);
        synchronized (this.turnHistory) {
            this.turnHistory.add(move);
        }
        if (board.isWinningTurn(move)) {
            gameOver(side);
        } else if (!this.gameOver && !board.isWinnable()) {
            gameOver(GomokuSide.UNKNOWN);
        }
    }

    private void gameOver(GomokuSide winner) {
        this.gameOver = true;
        switch (winner) {
            case WHITE:
                this.winner = this.playerWhite;
                break;
            case BLACK:
                this.winner = this.playerBlack;
                break;
        }
    }

}
