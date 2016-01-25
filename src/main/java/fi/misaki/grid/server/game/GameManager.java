package fi.misaki.grid.server.game;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.protocol.PushMessage;
import fi.misaki.grid.protocol.key.MessageContext;
import fi.misaki.grid.server.player.Player;
import fi.misaki.grid.server.player.PlayerManager;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.websocket.Session;

/**
 * Logic for managing the games by players.
 *
 * @author vlumi
 */
@Stateless
public class GameManager implements Serializable {

    private static final long serialVersionUID = -6450837604774940779L;

    private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());

    @Inject
    private PlayerManager playerManager;

    private final Map<Player, Game> games = Collections.synchronizedMap(new HashMap<>());

    /**
     * Handle the challenge request from one player to another.
     *
     * @param challenger Who made the challenge.
     * @param data The data part of the request.
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
            this.games.put(challenger, game);

            PushMessage message = createPushMessageTemplate(GameMessageDataType.CHALLENGE);
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
     *
     * @param challengee
     * @param data The data part of the request.
     * @throws fi.misaki.grid.protocol.InvalidRequestException
     */
    public void handleAcceptChallengeRequest(Player challengee, JsonObject data)
            throws InvalidRequestException {
        Player challenger = playerManager.getPlayerForName(data.getString("to", ""));

        synchronized (this.games) {
            Game game = this.games.get(challenger);
            if (!game.addPlayer(challengee)) {
                // TODO: error
                return;
            }
            this.games.put(challengee, game);
            game.start();
            sendStateToPlayers(game);
        }
    }

    /**
     *
     * @param challengee
     * @param data The data part of the request.
     * @throws fi.misaki.grid.protocol.InvalidRequestException
     */
    public void handleRejectChallengeRequest(Player challengee, JsonObject data)
            throws InvalidRequestException {
        Player challenger = playerManager.getPlayerForName(data.getString("to", ""));

        this.playerManager.setFree(challenger, challengee);

        synchronized (this.games) {
            this.games.remove(challenger);
        }

        PushMessage message = createPushMessageTemplate(GameMessageDataType.REJECT_CHALLENGE);
        message.getData()
                .add("from", challenger.getName())
                .add("to", challengee.getName());

        playerManager.sendMessage(challenger, message);
    }

    /**
     *
     * @param player
     * @param data The data part of the request.
     * @throws fi.misaki.grid.protocol.InvalidRequestException
     */
    public void handlePlacePieceRequest(Player player, JsonObject data)
            throws InvalidRequestException {
        Game game = this.games.get(player);
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
     *
     * @param player
     * @param data The data part of the request.
     */
    public void handleNewGame(Player player, JsonObject data) {
        synchronized (this.games) {
            Game game = this.games.get(player);
            game.start();
            if (game.isRunning()) {
                sendStateToPlayers(game);
            }
        }
    }

    /**
     *
     * @param player
     * @param data The data part of the request.
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
        synchronized (this.games) {
            Game game = this.games.get(player);
            if (game == null) {
                return;
            }
            playerManager.setFree(game.getPlayers().toArray(new Player[0]));
            PushMessage message = createLeaveMessage();
            playerManager.sendMessage(game.getPlayers(), message);
            game.leave(player);
            this.games.remove(player);
        }
    }

    /**
     * Finds the game the player is playing, if any.
     *
     * @param player
     * @return the found game, or null.
     */
    public Game findGame(Player player) {
        synchronized (this.games) {
            Game game = this.games.get(player);
            return game;
        }
    }

    /**
     *
     * @param game
     */
    public void sendStateToPlayers(Game game) {
        sendState(game, game.getPlayerWhite());
        sendState(game, game.getPlayerBlack());
    }

    /**
     *
     * @param game
     * @param player
     */
    public void sendState(Game game, Player player) {
        PushMessage message = createStateMessage(game, game.getSide(player));
        playerManager.sendMessage(player, message);
    }

    /**
     *
     * @param game
     * @param session
     */
    public void sendStateToSession(Game game, Session session) {
        Player player = this.playerManager.getPlayerForSessionId(session.getId());
        PushMessage message = createStateMessage(game, game.getSide(player));
        playerManager.sendMessage(session, message);
    }

    /**
     *
     * @param game
     */
    public void sendPlacePieceToPlayers(Game game) {
        PushMessage message = createPlacePieceMessage(game);
        playerManager.sendMessage(game.getPlayers(), message);
    }

    /**
     *
     * @param game
     */
    public void sendGameoverToPlayers(Game game) {
        // TODO: implement
        Player winner = game.getWinner();
        PushMessage message = createPushMessageTemplate(GameMessageDataType.GAME_OVER);
        if (winner != null) {
            message.getData()
                    .add("winner", game.getWinner().getName())
                    .add("positions", game.getWinningPositionsAsJsonArrayBuilder());
        }
        playerManager.sendMessage(game.getPlayers(), message);
    }

    /**
     *
     * @param game
     * @param side
     * @return
     */
    private PushMessage createStateMessage(Game game, GameSide side) {
        PushMessage message = createPushMessageTemplate(GameMessageDataType.STATE);
        message.getData()
                .add("opponent", game.getPlayer(side.getOther()).getName())
                .add("you", side.getValue())
                .add("turn", game.getCurrentSide().getValue())
                .add("moves", game.getMoveHistoryAsJsonArrayBuilder())
                .add("variant", game.getVariant().getValue());
        return message;
    }

    /**
     *
     * @param game
     * @param side
     * @return
     */
    private PushMessage createLeaveMessage() {
        PushMessage message = createPushMessageTemplate(GameMessageDataType.LEAVE);
        return message;
    }

    /**
     *
     * @param game
     * @param side
     * @return
     */
    private PushMessage createPlacePieceMessage(Game game) {
        PushMessage message = createPushMessageTemplate(GameMessageDataType.PLACE_PIECE);
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
     *
     * @param type
     * @return
     */
    private PushMessage createPushMessageTemplate(GameMessageDataType type) {
        PushMessage message = new PushMessage(MessageContext.GAME);
        message.getData()
                .add("type", type.getCode());
        return message;
    }

}
