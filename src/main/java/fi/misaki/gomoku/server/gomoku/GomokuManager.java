package fi.misaki.gomoku.server.gomoku;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.protocol.PushMessage;
import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.user.UserManager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;

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
     * @param user
     * @param data
     */
    public void handleStateRequest(User user, JsonObject data) {
        synchronized (this.games) {
            GomokuGame game = this.games.get(user);
            this.sendState(game, user);
        }
    }

    /**
     *
     * @param challenger
     * @param data
     */
    public void handleChallengeRequest(User challenger, JsonObject data) {
        User challengee = userManager.getUserForName(data.getString("to", ""));

        GomokuGame game = new GomokuGame();
        game.addPlayer(challenger);
        this.games.put(challenger, game);

        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.CHALLENGE);
        message.getData()
                .add("from", challenger.getName())
                .add("to", challengee.getName());

        userManager.sendMessageToUser(challengee, message);
    }

    /**
     *
     * @param challenger
     * @param data
     */
    public void handleCancelChallengeRequest(User challenger, JsonObject data) {
        User challengee = userManager.getUserForName(data.getString("to", ""));

        synchronized (this.games) {
            this.games.remove(challenger);
        }

        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.CANCEL_CHALLENGE);
        message.getData()
                .add("from", challenger.getName())
                .add("to", challengee.getName());

        userManager.sendMessageToUser(challengee, message);
    }

    /**
     *
     * @param challengee
     * @param data
     */
    public void handleAcceptChallengeRequest(User challengee, JsonObject data) {
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
        }
    }

    /**
     *
     * @param challengee
     * @param data
     */
    public void handleRejectChallengeRequest(User challengee, JsonObject data) {
        User challenger = userManager.getUserForName(data.getString("to", ""));

        synchronized (this.games) {
            this.games.remove(challenger);
        }

        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.REJECT_CHALLENGE);
        message.getData()
                .add("from", challenger.getName())
                .add("to", challengee.getName());

        userManager.sendMessageToUser(challenger, message);
    }

    /**
     *
     * @param user
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public void handlePlacePieceRequest(User user, JsonObject data)
            throws InvalidRequestException {
        GomokuGame game = this.games.get(user);
        if (game == null) {
            // TODO: error
            throw new InvalidRequestException("No game found.");
        }
        GomokuSide side = game.getSide(user);
        if (side != game.getCurrentTurn()) {
            // TODO: error
            throw new InvalidRequestException("Not your turn.");
        }

        int row = data.getInt("row");
        int column = data.getInt("column");
        if (!game.placePiece(column, row)) {
            // TODO: error
            throw new InvalidRequestException("Not a valid move.");
        }

        sendPlacePieceToPlayers(game);
        if (game.isGameOver()) {
            sendGameoverToPlayers(game);
        }

    }

    /**
     *
     * @param user
     * @param data
     */
    public void handleLeaveRequest(User user, JsonObject data) {
        synchronized (this.games) {
            GomokuGame game = this.games.get(user);
            game.leave(user);
            this.games.remove(user);
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
        userManager.sendMessageToUser(player, message);
    }

    /**
     *
     * @param game
     * @param player
     */
    public void sendPlacePiece(GomokuGame game, User player) {
        PushMessage message = createPlacePieceMessage(game, game.getSide(player));
        userManager.sendMessageToUser(player, message);
    }

    /**
     *
     * @param game
     */
    public void sendGameoverToPlayers(GomokuGame game) {
        // TODO: implement
        User winner = game.getWinner();
        User loser = game.getLoser();
        if (winner == null) {
            final Set<User> players = new HashSet<>();
            players.add(game.getPlayerWhite());
            players.add(game.getPlayerBlack());

            PushMessage message = createPushMessageTemplate(GomokuMessageDataType.GAME_OVER);
            // TODO: add data
            message.getData();
            userManager.sendMessageToUsers(players, message);
        } else {
            PushMessage winMessage = createPushMessageTemplate(GomokuMessageDataType.GAME_OVER);
            // TODO: add data
            winMessage.getData();
            userManager.sendMessageToUser(winner, winMessage);

            PushMessage loseMessage = createPushMessageTemplate(GomokuMessageDataType.GAME_OVER);
            // TODO: add data
            loseMessage.getData();
            userManager.sendMessageToUser(loser, loseMessage);
        }
    }

    /**
     *
     * @param game
     */
    public void sendPlacePieceToPlayers(GomokuGame game) {
        sendPlacePiece(game, game.getPlayerWhite());
        sendPlacePiece(game, game.getPlayerBlack());
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
    private PushMessage createPlacePieceMessage(GomokuGame game, GomokuSide side) {
        PushMessage message = createPushMessageTemplate(GomokuMessageDataType.PLACE_PIECE);
        GomokuGameTurn lastTurn = game.getLastTurn();
        message.getData()
                .add("opponent", game.getPlayer(side.getOther()).getName())
                .add("you", side.getValue())
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
        PushMessage message = new PushMessage(MessageType.GOMOKU);
        message.getData()
                .add("type", type.getCode());
        return message;
    }

}
