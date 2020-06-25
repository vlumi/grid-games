package fi.misaki.grid.server.game;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.protocol.PushMessage;
import fi.misaki.grid.protocol.key.MessageContext;
import fi.misaki.grid.server.player.Player;
import fi.misaki.grid.server.player.PlayerManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.websocket.Session;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Logic for managing the games by players.
 *
 * @author vlumi
 */
@ApplicationScoped
public class GameManager implements Serializable {

    private static final long serialVersionUID = -6450837604774940779L;

    private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());

    @Inject
    private PlayerManager playerManager;

    /**
     * Games mapped by player.
     */
    private final Map<Player, Game> gamesByPlayer = Collections.synchronizedMap(new HashMap<>());

    /**
     * Handle the challenge request from one player to another.
     *
     * @param challenger Who made the challenge.
     * @param data       The data part of the request.
     * @throws fi.misaki.grid.protocol.InvalidRequestException
     */
    public void handleChallengeRequest(Player challenger, JsonObject data)
            throws InvalidRequestException {
        Player challengee = playerManager.getPlayerForName(data.getString("to", ""));
        GameVariant variant = GameVariant.ofValue(data.getString("variant"));

        if (challengee == null) {
            throw new InvalidRequestException("Challengee not found.");
        }
        if (variant == GameVariant.UNKNOWN) {
            throw new InvalidRequestException("Unknown variant.");
        }

        this.playerManager.setBusy(challenger, challengee);
        try {
            Game game = new Game(variant);
            game.addPlayer(challenger);
            game.addPlayer(challengee);
            this.gamesByPlayer.put(challenger, game);

            PushMessage message = createPushMessageTemplate(GamePushMessageDataType.CHALLENGE);
            message.getData()
                    .add("from", challenger.getName())
                    .add("to", challengee.getName())
                    .add("variant", variant.getValue());

            this.playerManager.sendMessage(challengee, message);
        } catch (Exception e) {
            this.playerManager.setFree(challenger, challengee);
            throw e;
        }
    }

    /**
     * Handle the accept challenge request, starting the game.
     *
     * @param challengee The player who was challenged and accepted.
     * @param data       The data part of the request.
     * @throws fi.misaki.grid.protocol.InvalidRequestException
     */
    public void handleAcceptChallengeRequest(Player challengee, JsonObject data)
            throws InvalidRequestException {
        Player challenger = playerManager.getPlayerForName(data.getString("to", ""));

        synchronized (this.gamesByPlayer) {
            Game game = this.gamesByPlayer.get(challenger);
            if (game == null) {
                throw new InvalidRequestException("Game for challenger " + challenger.getName() + " not found.");
            }
            if (!game.addPlayer(challengee)) {
                throw new InvalidRequestException("Adding player to game failed.");
            }
            this.gamesByPlayer.put(challengee, game);
            game.start();
            sendStateToPlayers(game);
        }
    }

    /**
     * Handles the reject challenge request.
     *
     * @param challengee The player who was challenged and rejected.
     * @param data       The data part of the request.
     * @throws fi.misaki.grid.protocol.InvalidRequestException
     */
    public void handleRejectChallengeRequest(Player challengee, JsonObject data)
            throws InvalidRequestException {
        Player challenger = playerManager.getPlayerForName(data.getString("to", ""));

        this.playerManager.setFree(challenger, challengee);

        synchronized (this.gamesByPlayer) {
            this.gamesByPlayer.remove(challenger);
        }

        PushMessage message = createPushMessageTemplate(GamePushMessageDataType.LEAVE);
        message.getData();

        playerManager.sendMessage(challenger, message);
    }

    /**
     * Handles the place piece request.
     *
     * @param player The player making the move
     * @param data   The data part of the request.
     * @throws fi.misaki.grid.protocol.InvalidRequestException in case of an
     *                                                         invalid move.
     */
    public void handlePlacePieceRequest(Player player, JsonObject data)
            throws InvalidRequestException {
        Game game = this.gamesByPlayer.get(player);
        if (game == null) {
            throw new InvalidRequestException("No game found.");
        }
        GameSide side = game.getSide(player);
        if (side != game.getCurrentSide()) {
            throw new InvalidRequestException("Not your turn.");
        }

        int row = data.getInt("row");
        int column = data.getInt("column");
        if (!game.placePiece(column, row)) {
            throw new InvalidRequestException("Not a valid move.");
        }

        sendPlacePieceToPlayers(game);
        if (game.isGameOver()) {
            sendGameoverToPlayers(game);
        }

    }

    /**
     * Handles the new game request, starting the player when both players have
     * chosen to start a new game.
     *
     * @param player The player choosing to start a new game.
     * @param data   The data part of the request.
     */
    public void handleNewGame(Player player, JsonObject data) {
        synchronized (this.gamesByPlayer) {
            Game game = this.gamesByPlayer.get(player);
            game.start();
            if (game.isRunning()) {
                sendStateToPlayers(game);
            }
        }
    }

    /**
     * Handles the leave request, removing the player from the game and
     * terminating it.
     *
     * @param player The player leaving from the game.
     * @param data   The data part of the request.
     */
    public void handleLeaveRequest(Player player, JsonObject data) {
        leavePlayer(player);
    }

    /**
     * Removes the player from the game, ending the game.
     *
     * @param player
     */
    public void leavePlayer(Player player) {
        synchronized (this.gamesByPlayer) {
            Game game = this.gamesByPlayer.get(player);
            if (game == null) {
                return;
            }
            playerManager.setFree(game.getPlayers().toArray(new Player[0]));
            PushMessage message = createLeaveMessage();
            playerManager.sendMessage(game.getPlayers(), message);
            game.leave(player);
            this.gamesByPlayer.remove(player);
        }
    }

    /**
     * Finds the game the player is playing, if any.
     *
     * @param player
     * @return the found game, or null.
     */
    public Game findGame(Player player) {
        synchronized (this.gamesByPlayer) {
            Game game = this.gamesByPlayer.get(player);
            return game;
        }
    }

    /**
     * Send the state of the game to both players.
     *
     * @param game The game whose status to send.
     */
    public void sendStateToPlayers(Game game) {
        sendState(game, game.getPlayerWhite());
        sendState(game, game.getPlayerBlack());
    }

    /**
     * Send the state of the game to the given player.
     *
     * @param game   The game whose status to send.
     * @param player The player whom to send.
     */
    private void sendState(Game game, Player player) {
        PushMessage message = createStateMessage(game, game.getSide(player));
        playerManager.sendMessage(player, message);
    }

    /**
     * Send the state of the game to the given session to the given WebSocket
     * session.
     *
     * @param game    The game whose status to send.
     * @param session The WebSocket session to send the message.
     * @throws InvalidRequestException
     */
    public void sendStateToSession(Game game, Session session)
            throws InvalidRequestException {
        Player player = this.playerManager.getPlayerForSessionId(session.getId());
        PushMessage message = createStateMessage(game, game.getSide(player));
        playerManager.sendMessage(session, message);
    }

    /**
     * Sends the place piece event to both players in the game.
     *
     * @param game The associated game.
     */
    public void sendPlacePieceToPlayers(Game game) {
        PushMessage message = createPlacePieceMessage(game);
        playerManager.sendMessage(game.getPlayers(), message);
    }

    /**
     * Sends the game over event to both players.
     *
     * @param game The associated game.
     */
    public void sendGameoverToPlayers(Game game) {
        // TODO: implement
        Player winner = game.getWinner();
        PushMessage message = createPushMessageTemplate(GamePushMessageDataType.GAME_OVER);
        if (winner != null) {
            message.getData()
                    .add("winner", game.getWinner().getName())
                    .add("positions", game.getWinningPositionsAsJsonArrayBuilder());
        }
        playerManager.sendMessage(game.getPlayers(), message);
    }

    /**
     * Creates a state event message, to be sent to players.
     *
     * @param game The associated game.
     * @param side The side to whom the message will be sent.
     * @return The message.
     */
    private PushMessage createStateMessage(Game game, GameSide side) {
        PushMessage message = createPushMessageTemplate(GamePushMessageDataType.STATE);
        message.getData()
                .add("opponent", game.getPlayer(side.getOther()).getName())
                .add("you", side.getValue())
                .add("turn", game.getCurrentSide().getValue())
                .add("moves", game.getMoveHistoryAsJsonArrayBuilder())
                .add("variant", game.getVariant().getValue());
        return message;
    }

    /**
     * Creates a leave event message, to be sent to players.
     *
     * @param game The associated game.
     * @return The message.
     */
    private PushMessage createLeaveMessage() {
        PushMessage message = createPushMessageTemplate(GamePushMessageDataType.LEAVE);
        return message;
    }

    /**
     * Creates a place piece event message, to be sent to players.
     *
     * @param game The associated game.
     * @return The message.
     */
    private PushMessage createPlacePieceMessage(Game game) {
        PushMessage message = createPushMessageTemplate(GamePushMessageDataType.PLACE_PIECE);
        List<GameBoardPosition> lastMoves = game.getLastMoves();
        if (lastMoves.size() > 0) {
            GameSide side = lastMoves.get(0).getSide();

            JsonArrayBuilder moves = Json.createArrayBuilder();
            lastMoves.stream()
                    .map(turn -> Json.createArrayBuilder()
                            .add(turn.getColumn())
                            .add(turn.getRow()))
                    .forEachOrdered(turn -> {
                        moves.add(turn);
                    });

            message.getData()
                    .add("turn", game.getCurrentSide().getValue())
                    .add("side", side.getValue())
                    .add("moves", moves);
        }
        return message;
    }

    /**
     * Creates a push message template, for messages to be sent to players.
     *
     * @param type The game message type.
     * @return The message, ready for filling in the rest of the fields.
     */
    private PushMessage createPushMessageTemplate(GamePushMessageDataType type) {
        PushMessage message = new PushMessage(MessageContext.GAME);
        message.getData()
                .add("type", type.getCode());
        return message;
    }

}
