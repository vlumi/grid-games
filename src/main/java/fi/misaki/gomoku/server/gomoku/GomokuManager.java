package fi.misaki.gomoku.server.gomoku;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.protocol.PushMessage;
import fi.misaki.gomoku.protocol.key.MessageContext;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.user.UserManager;
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
public class GomokuManager implements Serializable {

    private static final long serialVersionUID = -6450837604774940779L;

    private static final Logger LOGGER = Logger.getLogger(GomokuManager.class.getName());

    @Inject
    private UserManager userManager;

    private final Map<User, GomokuGame> games = Collections.synchronizedMap(new HashMap<>());

    /**
     *
     * @param challenger
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public void handleChallengeRequest(User challenger, JsonObject data)
            throws InvalidRequestException {
        User challengee = userManager.getUserForName(data.getString("to", ""));

        if (challengee == null) {
            throw new InvalidRequestException("Challengee not found.");
        }

        GomokuGame game = new GomokuGame();
        game.addPlayer(challenger);
        this.games.put(challenger, game);

        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.CHALLENGE);
        message.getData()
                .add("from", challenger.getName())
                .add("to", challengee.getName());

        userManager.sendMessage(challengee, message);
        // TODO: send user state -- busy
    }

    /**
     *
     * @param challenger
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public void handleCancelChallengeRequest(User challenger, JsonObject data)
            throws InvalidRequestException {
        User challengee = userManager.getUserForName(data.getString("to", ""));

        synchronized (this.games) {
            this.games.remove(challenger);
        }

        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.CANCEL_CHALLENGE);
        message.getData()
                .add("from", challenger.getName())
                .add("to", challengee.getName());

        userManager.sendMessage(challengee, message);
    }

    /**
     *
     * @param challengee
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public void handleAcceptChallengeRequest(User challengee, JsonObject data)
            throws InvalidRequestException {
        User challenger = userManager.getUserForName(data.getString("to", ""));

        synchronized (this.games) {
            GomokuGame game = this.games.get(challenger);
            if (!game.addPlayer(challengee)) {
                // TODO: error
                return;
            }
            this.games.put(challengee, game);
            game.start();
            sendStateToPlayers(game);
            // TODO: send user state -- busy
        }
    }

    /**
     *
     * @param challengee
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public void handleRejectChallengeRequest(User challengee, JsonObject data)
            throws InvalidRequestException {
        User challenger = userManager.getUserForName(data.getString("to", ""));

        synchronized (this.games) {
            this.games.remove(challenger);
        }

        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.REJECT_CHALLENGE);
        message.getData()
                .add("from", challenger.getName())
                .add("to", challengee.getName());

        userManager.sendMessage(challenger, message);
    }

    /**
     *
     * @param player
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public void handlePlacePieceRequest(User player, JsonObject data)
            throws InvalidRequestException {
        GomokuGame game = this.games.get(player);
        if (game == null) {
            throw new InvalidRequestException("No game found.");
        }
        GomokuSide side = game.getSide(player);
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
    public void handleNewGame(User player, JsonObject data) {
        synchronized (this.games) {
            GomokuGame game = this.games.get(player);
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
    public void handleLeaveRequest(User player, JsonObject data) {
        synchronized (this.games) {
            GomokuGame game = this.games.get(player);
            game.leave(player);
            this.games.remove(player);
            // TODO: send user state -- free, both users
        }
    }

    public GomokuGame findGame(User player) {
        synchronized (this.games) {
            GomokuGame game = this.games.get(player);
            return game;
        }
    }

    /**
     *
     * @param game
     */
    public void sendStateToPlayers(GomokuGame game) {
        sendState(game, game.getPlayerWhite());
        sendState(game, game.getPlayerBlack());
    }

    /**
     *
     * @param game
     * @param player
     */
    public void sendState(GomokuGame game, User player) {
        PushMessage message = createStateMessage(game, game.getSide(player));
        userManager.sendMessage(player, message);
    }

    /**
     *
     * @param game
     * @param session
     */
    public void sendStateToSession(GomokuGame game, Session session) {
        User player = this.userManager.getUserForSessionId(session.getId());
        PushMessage message = createStateMessage(game, game.getSide(player));
        userManager.sendMessage(session, message);
    }

    /**
     *
     * @param game
     */
    public void sendPlacePieceToPlayers(GomokuGame game) {
        PushMessage message = createPlacePieceMessage(game);
        userManager.sendMessage(game.getPlayers(), message);
    }

    /**
     *
     * @param game
     */
    public void sendGameoverToPlayers(GomokuGame game) {
        // TODO: implement
        User winner = game.getWinner();
        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.GAME_OVER);
        if (winner != null) {
            message.getData()
                    .add("winner", game.getWinner().getName());
        }
        userManager.sendMessage(game.getPlayers(), message);
    }

    /**
     *
     * @param game
     * @param side
     * @return
     */
    private PushMessage createStateMessage(GomokuGame game, GomokuSide side) {
        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.STATE);
        message.getData()
                .add("opponent", game.getPlayer(side.getOther()).getName())
                .add("you", side.getValue())
                .add("turn", game.getCurrentTurn().getValue())
                .add("moves", game.getTurnHistoryAsJsonArrayBuilder());
        return message;
    }

    /**
     *
     * @param game
     * @param side
     * @return
     */
    private PushMessage createPlacePieceMessage(GomokuGame game) {
        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.PLACE_PIECE);
        GomokuGameTurn lastTurn = game.getLastTurn();
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
    private PushMessage createPushMessageTemplate(GomokuMessageDataType type) {
        PushMessage message = new PushMessage(MessageContext.GOMOKU);
        message.getData()
                .add("type", type.getCode());
        return message;
    }

}
