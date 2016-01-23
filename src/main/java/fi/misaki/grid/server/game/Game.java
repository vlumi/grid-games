/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.game;

import fi.misaki.grid.server.player.Player;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;

/**
 * A game instance, tracking and handling changes to the current state.
 *
 * @author vlumi
 */
public class Game implements Serializable {

    private static final long serialVersionUID = -1005530574236302298L;

    private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());

    /**
     * The backing game board.
     */
    private final GameBoard board;
    /**
     * All moves done during this game, in order.
     */
    private final Deque<GameBoardPosition> moveHistory = new ConcurrentLinkedDeque<>();
    /**
     * The variant of the current game.
     */
    private GameVariant variant;
    /**
     * The player controlling the white pieces.
     */
    private Player playerWhite;
    /**
     * The player controlling the black pieces.
     */
    private Player playerBlack;
    /**
     * The player declared as the winner, when the game is over and is not a
     * tie.
     */
    private Player winner = null;
    /**
     * The player declared as the loser, when the game is over and is not a tie.
     */
    private Player loser = null;
    /**
     * Whether the game is on.
     */
    private boolean running = false;
    /**
     * Whether the game has ended.
     */
    private boolean gameOver = false;
    /**
     * Whose turn is it to make a move.
     */
    private GameSide currentSide;

    /**
     * Standard constructor.
     *
     * @param variant The variant of the game to create.
     */
    public Game(GameVariant variant) {
        this.variant = variant;
        this.board = new GameBoard(variant);
    }

    /**
     * Add a player to the game. Two games must be added to the game before it
     * can be started; no more, no less.
     *
     * The first player is assigned as the white player.
     *
     * @param player The player to add.
     * @return true if the player could be added.
     */
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

    /**
     * Attempt to start the game.
     *
     * This method should be called twice (once by each player) before the game
     * actually starts.
     */
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
                this.currentSide = GameSide.WHITE;
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
     * Attempt to place a piece on the board, by the current side.
     *
     * @param column The column of the target position.
     * @param row The row of the target position.
     * @return <code>true</code> if the move was successful.
     */
    public boolean placePiece(int column, int row) {
        if (!this.running) {
            return false;
        }
        synchronized (this.board) {
            boolean turnSuccessful = false;
            switch (this.currentSide) {
                case WHITE:
                    if (this.board.placeWhitePiece(column, row)) {
                        turnSuccessful = true;
                    }
                    break;
                case BLACK:
                    if (this.board.placeBlackPiece(column, row)) {
                        turnSuccessful = true;
                    }
                    break;
            }

            if (turnSuccessful) {
                recordTurn(column, row, this.currentSide);
                if (!this.gameOver) {
                    this.currentSide = this.currentSide.getOther();
                }
            }
            return turnSuccessful;
        }
    }

    /**
     * Handle the player leaving the game, essentially terminating the game.
     *
     * @param player The player initiating the game termination.
     */
    public void leave(Player player) {
        // TODO: implement
        terminate();
    }

    /**
     * Terminate the game.
     */
    private void terminate() {
        this.playerWhite = null;
        this.playerBlack = null;
        this.winner = null;
        this.loser = null;

        this.running = false;
    }

    /**
     * Check if the game is currently active and running.
     *
     * @return true if the game has started and not ended.
     */
    public boolean isRunning() {
        return this.running && !this.gameOver;
    }

    /**
     * Get the side currently allowed to make a move.
     *
     * @return The current side.
     */
    public GameSide getCurrentSide() {
        return this.currentSide;
    }

    /**
     * Whether the game is over.
     *
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Get the variant of the game.
     *
     * @return the variant.
     */
    public GameVariant getVariant() {
        return variant;
    }

    /**
     * Get the participating players.
     *
     * @return the participating players as a set.
     */
    public Set<Player> getPlayers() {
        final Set<Player> players = new HashSet<>();
        players.add(getPlayerWhite());
        players.add(getPlayerBlack());
        return players;
    }

    /**
     * Get the winner, if any.
     *
     * @return the winner, or null.
     */
    public Player getWinner() {
        return this.winner;
    }

    /**
     * Get the loser, if any.
     *
     * @return the loser, or null.
     */
    public Player getLoser() {
        return this.loser;
    }

    /**
     * Get the white player, if any.
     *
     * @return the white player, or null.
     */
    public Player getPlayerWhite() {
        return this.playerWhite;
    }

    /**
     * Get the black player, if any.
     *
     * @return the black player, or null.
     */
    public Player getPlayerBlack() {
        return this.playerBlack;
    }

    /**
     * Get the player on the given side.
     *
     * @param side The side to get the player on.
     * @return The player playing on the given side.
     */
    public Player getPlayer(GameSide side) {
        switch (side) {
            case WHITE:
                return this.playerWhite;
            case BLACK:
                return this.playerBlack;
        }
        return null;
    }

    /**
     * Get the side of the given player.
     *
     * @param player The player whose side should be returned.
     * @return The side of the player, or UNKNOWN if not playing.
     */
    public GameSide getSide(Player player) {
        if (this.playerWhite == player) {
            return GameSide.WHITE;
        } else if (this.playerBlack == player) {
            return GameSide.BLACK;
        } else {
            return GameSide.UNKNOWN;
        }
    }

    /**
     * Get the full move history of the game.
     *
     * @return all the moves in the game so far.
     */
    public List<GameBoardPosition> getMoveHistory() {
        synchronized (this.moveHistory) {
            return this.moveHistory.stream().collect(Collectors.toList());
        }
    }

    /**
     * Convenience method for getMoveHistory.
     *
     * @return
     */
    public JsonArrayBuilder getMoveHistoryAsJsonArrayBuilder() {
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
     * Get the last move(s) of the game. Returns the last move, and any
     * immediately preceeding moves done by the same player.
     *
     * @return a list of moves.
     */
    public List<GameBoardPosition> getLastMoves() {
        List<GameBoardPosition> lastMoves = new ArrayList<>();
        synchronized (this.moveHistory) {
            GameSide lastMoveSide = this.moveHistory.peekLast().getSide();
            Iterator<GameBoardPosition> iter = this.moveHistory.descendingIterator();
            while (iter.hasNext()) {
                GameBoardPosition position = iter.next();
                if (position.getSide() == lastMoveSide) {
                    lastMoves.add(position);
                } else {
                    break;
                }
            }
        }
        return lastMoves;
    }

    /**
     * Get the winning positions, if the game is over and did not end as a tie.
     *
     * @return the list of positions constituting the winning sequence.
     */
    public List<GameBoardPosition> getWinningPositions() {
        synchronized (this.board) {
            return this.board.getWinningPositions();
        }
    }

    /**
     * Convenience method for getWinningPositions.
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

    /**
     * Mark the game as ended, with the given winner.
     *
     * @param winner
     */
    private void gameOver(GameSide winner) {
        LOGGER.log(Level.FINE, "Game over! winner: {0}", winner);
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
