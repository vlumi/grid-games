/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.game;

import fi.misaki.gomoku.server.player.Player;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author vlumi
 */
public class Game {

    private final static GameVariant DEFAULT_VARIANT = GameVariant.TICTACTOE;

    private final GameBoard board;

    private final Deque<GameBoardPosition> moveHistory = new ConcurrentLinkedDeque<>();
    private boolean gameOver = false;

    private GameVariant variant;
    private Player playerWhite;
    private Player playerBlack;
    private Player winner = null;
    private Player loser = null;
    private GameSide currentTurn;
    private boolean running = false;

    public Game() {
        this(DEFAULT_VARIANT);
    }

    public Game(GameVariant variant) {
        this.variant = variant;
        this.board = new GameBoard(
                variant.getColumns(),
                variant.getRows(),
                variant.getWinLength());
    }

    public boolean addPlayer(Player player) {
        if (this.playerWhite == null) {
            this.playerWhite = player;
            return true;
        }
        if (this.playerBlack == null && this.playerWhite != player) {
            this.playerBlack = player;
            return true;
        }
        return false;
    }

    public void start() {
        synchronized (this) {
            if (this.running && this.gameOver) {
                // After the previous game, require two starts, one from each player.
                this.running = false;
                this.gameOver = false;
                return;
            }
            if (this.playerWhite != null
                    && this.playerBlack != null
                    && !this.gameOver) {
                this.currentTurn = GameSide.WHITE;
                this.running = true;
            }
            if (this.winner != null && this.loser != null) {
                this.playerWhite = this.winner;
                this.playerBlack = this.loser;
            }
            this.board.reset();
            this.moveHistory.clear();
            this.winner = null;
            this.loser = null;
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
        synchronized (this.board) {
            boolean turnSuccessful = false;
            Player currentPlayer = null;
            Player nextPlayer = null;
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
                if (!this.gameOver) {
                    this.currentTurn = this.currentTurn.getOther();
                }
            }
            return turnSuccessful;
        }
    }

    public void leave(Player player) {
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

    public boolean isRunning() {
        return this.running && !this.gameOver;
    }

    public GameSide getCurrentTurn() {
        return this.currentTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public GameVariant getVariant() {
        return variant;
    }

    public Set<Player> getPlayers() {
        final Set<Player> players = new HashSet<>();
        players.add(getPlayerWhite());
        players.add(getPlayerBlack());
        return players;
    }

    public Player getWinner() {
        return this.winner;
    }

    public Player getLoser() {
        return this.loser;
    }

    public Player getPlayerWhite() {
        return this.playerWhite;
    }

    public Player getPlayerBlack() {
        return this.playerBlack;
    }

    public Player getPlayer(GameSide side) {
        switch (side) {
            case WHITE:
                return this.playerWhite;
            case BLACK:
                return this.playerBlack;
        }
        return null;
    }

    public GameSide getSide(Player player) {
        if (this.playerWhite == player) {
            return GameSide.WHITE;
        } else if (this.playerBlack == player) {
            return GameSide.BLACK;
        } else {
            return GameSide.UNKNOWN;
        }
    }

    public List<GameBoardPosition> getMoveHistory() {
        synchronized (this.moveHistory) {
            return this.moveHistory.stream().collect(Collectors.toList());
        }
    }

    public GameBoardPosition getLastMove() {
        synchronized (this.moveHistory) {
            return this.moveHistory.peekLast();
        }
    }

    public JsonArrayBuilder getTurnHistoryAsJsonArrayBuilder() {
        synchronized (this.moveHistory) {
            JsonArrayBuilder turns = Json.createArrayBuilder();
            this.moveHistory.stream()
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
     *
     * @return
     */
    public List<GameBoardPosition> getWinningPositions() {
        synchronized (this.board) {
            return this.board.getWinningPositions();
        }
    }

    /**
     *
     * @return
     */
    public JsonArrayBuilder getWinningPositionsAsJsonArrayBuilder() {
        synchronized (this.board) {
            JsonArrayBuilder positions = Json.createArrayBuilder();
            this.board.getWinningPositions().stream()
                    .map(position -> Json.createArrayBuilder()
                            .add(position.getSide().getValue())
                            .add(position.getColumn())
                            .add(position.getRow()))
                    .forEachOrdered(position -> positions.add(position));
            return positions;
        }
    }

    /**
     * Record the move made.
     *
     * @param column
     * @param row
     * @param side
     */
    private void recordTurn(int column, int row, GameSide side) {
        GameBoardPosition move = new GameBoardPosition(column, row, side);
        synchronized (this.moveHistory) {
            this.moveHistory.add(move);
        }
        if (board.isWinningTurn(move)) {
            gameOver(side);
        } else if (!this.gameOver && !board.isWinnable()) {
            gameOver(GameSide.UNKNOWN);
        }
    }

    private void gameOver(GameSide winner) {
        System.out.println("Game over! winner: " + winner);
        this.gameOver = true;
        switch (winner) {
            case WHITE:
                this.winner = this.playerWhite;
                this.loser = this.playerBlack;
                break;
            case BLACK:
                this.winner = this.playerBlack;
                this.loser = this.playerWhite;
                break;
            default:
                this.winner = null;
                this.loser = null;
        }
    }

}
