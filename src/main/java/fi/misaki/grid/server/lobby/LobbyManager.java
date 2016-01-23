package fi.misaki.grid.server.lobby;

import fi.misaki.grid.protocol.Message;
import fi.misaki.grid.protocol.key.MessageContext;
import fi.misaki.grid.protocol.PushMessage;
import fi.misaki.grid.server.player.Player;
import fi.misaki.grid.server.player.PlayerManager;
import java.io.Serializable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;

/**
 *
 * @author vlumi
 */
@Stateless
public class LobbyManager implements Serializable {

    private static final long serialVersionUID = 3409124989853945066L;

    private static final Logger LOGGER = Logger.getLogger(LobbyManager.class.getName());

    @Inject
    private PlayerManager playerManager;

    public void handleChatMessageRequest(Player player, JsonObject data) {
        String message = data.getString("message", "");
        String to = data.getString("to", "");

        LOGGER.log(Level.FINEST, "Lobby message to {0}: {1}", new String[]{to, message});

        if (to.isEmpty()) {
            sendChatMessageToAll(player, message);
        } else {
            boolean isPrivate = data.getBoolean("private", false);
            Player toPlayer = playerManager.getPlayerForName(to);
            sendChatMessage(player, toPlayer, message, isPrivate);
        }
    }

    /**
     *
     * @param player
     */
    public void sendInitMessage(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.INIT);
        message.getData()
                .add("members", this.playerManager.getFreeMembersAsJsonArrayBuilder());
        playerManager.sendMessage(player, message);
    }

    /**
     * Send a join message to all open sessions.
     *
     * @param player The player who joined.
     */
    public void sendJoinMessage(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.JOIN);
        message.getData()
                .add("name", player.getName());

        LOGGER.log(Level.FINEST, "LOBBY JOIN MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     * Send a part message to all open sessions.
     *
     * @param player The player who left.
     */
    public void sendPartMessage(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.PART);
        message.getData()
                .add("name", player.getName());

        LOGGER.log(Level.FINEST, "LOBBY PART MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     *
     * @param from
     * @param messageText
     */
    public void sendChatMessageToAll(Player from, String messageText) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.CHAT_MESSAGE);
        message.getData()
                .add("from", from.getName())
                .add("message", messageText);

        LOGGER.log(Level.FINEST, "LOBBY MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     *
     * @param from
     * @param to
     * @param messageText
     * @param isPrivate
     */
    public void sendChatMessage(Player from, Player to, String messageText, boolean isPrivate) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.CHAT_MESSAGE);
        message.getData()
                .add("from", from.getName())
                .add("to", to.getName())
                .add("message", messageText)
                .add("private", isPrivate);

        LOGGER.log(Level.FINEST, "LOBBY MESSAGE: {0}", message);
        if (isPrivate) {
            final Set<Player> targetPlayers = Stream.of(from, to).collect(Collectors.toSet());
            this.playerManager.sendMessage(targetPlayers, message);
        } else {
            sendMessageToAllSessions(message);
        }
    }

    public void sendPlayerBusy(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.STATUS);
        message.getData()
                .add("name", player.getName())
                .add("status", "busy");
        this.sendMessageToAllSessions(message);
    }

    public void sendPlayerFree(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.STATUS);
        message.getData()
                .add("name", player.getName())
                .add("status", "free");
        this.sendMessageToAllSessions(message);
    }

    /**
     *
     * @param type
     * @return
     */
    private PushMessage createPushMessageTemplate(LobbyMessageDataType type) {
        PushMessage message = new PushMessage(MessageContext.LOBBY);
        message.getData()
                .add("type", type.getCode());
        return message;
    }

    /**
     *
     * @param message
     */
    private void sendMessageToAllSessions(Message message) {
        String messageString = message.toJsonObject().toString();
        playerManager.getAllSessions().forEach(session -> {
            LOGGER.log(Level.FINEST, "send to session: {0}", session.getId());
            session.getAsyncRemote().sendText(messageString);
        });
    }

}
