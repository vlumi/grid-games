package fi.misaki.gomoku.server.game;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.protocol.PushMessage;
import fi.misaki.gomoku.protocol.key.MessageContext;
import fi.misaki.gomoku.server.player.Player;
import fi.misaki.gomoku.server.player.PlayerManager;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.websocket.Session;

/**
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
     *
     * @param challenger
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
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
     * @param challenger
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public void handleCancelChallengeRequest(Player challenger, JsonObject data)
            throws InvalidRequestException {
        Player challengee = this.playerManager.getPlayerForName(data.getString("to", ""));

        this.playerManager.setFree(challenger, challengee);

        synchronized (this.games) {
            this.games.remove(challenger);
        }

        PushMessage message = createPushMessageTemplate(GameMessageDataType.CANCEL_CHALLENGE);
        message.getData()
                .add("from", challenger.getName())
                .add("to", challengee.getName());

        playerManager.sendMessage(challengee, message);
    }

    /**
     *
     * @param challengee
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
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
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
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
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public void handlePlacePieceRequest(Player player, JsonObject data)
            throws InvalidRequestException {
        Game game = this.games.get(player);
        if (game == null) {
            throw new InvalidRequestException("No game found.");
        }
        GameSide side = game.getSide(player);
        if (side != game.getCurrentTurn()) {
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
     * @param data
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
     * @param data
     */
    public void handleLeaveRequest(Player player, JsonObject data) {
        synchronized (this.games) {
            Game game = this.games.get(player);
            playerManager.setFree(game.getPlayers().toArray(new Player[0]));
            game.leave(player);
            this.games.remove(player);
        }
    }

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
                .add("turn", game.getCurrentTurn().getValue())
                .add("moves", game.getTurnHistoryAsJsonArrayBuilder())
                .add("variant", game.getVariant().getValue());
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
        GameBoardPosition lastTurn = game.getLastMove();
        message.getData()
                .add("turn", game.getCurrentTurn().getValue())
                .add("column", lastTurn.getColumn())
                .add("row", lastTurn.getRow())
                .add("side", lastTurn.getSide().getValue());
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
