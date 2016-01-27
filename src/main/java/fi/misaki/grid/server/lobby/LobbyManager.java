package fi.misaki.grid.server.lobby;

import fi.misaki.grid.protocol.InvalidRequestException;
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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

/**
 * Logic for managing the lobby
 *
 * @author vlumi
 */
@ApplicationScoped
public class LobbyManager implements Serializable {

    private static final long serialVersionUID = 3409124989853945066L;

    private static final Logger LOGGER = Logger.getLogger(LobbyManager.class.getName());

    @Inject
    private PlayerManager playerManager;

    /**
     * Handles a chat message received from the client.
     *
     * @param player The player who the message came from.
     * @param data The data object from the received message.
     * @throws InvalidRequestException
     */
    public void handleChatMessageRequest(Player player, JsonObject data)
            throws InvalidRequestException {
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
     * Sends the initialization message to the player.
     *
     * @param player The player to send the message.
     */
    public void sendInitMessage(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyPushMessageDataType.INIT);
        message.getData()
                .add("members", this.playerManager.getMembersAsJsonArrayBuilder())
                .add("busyMembers", this.playerManager.getBusyMembersAsJsonArrayBuilder());
        playerManager.sendMessage(player, message);
    }

    /**
     * Sends a join message to all open sessions.
     *
     * @param player The player who joined.
     */
    public void sendJoinMessage(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyPushMessageDataType.JOIN);
        message.getData()
                .add("name", player.getName());

        LOGGER.log(Level.FINEST, "LOBBY JOIN MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     * Sends a part message to all open sessions.
     *
     * @param player The player who left.
     */
    public void sendPartMessage(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyPushMessageDataType.PART);
        message.getData()
                .add("name", player.getName());

        LOGGER.log(Level.FINEST, "LOBBY PART MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     * Sends a message to all connected WebSocket sessions.
     *
     * @param from The player who the message is from.
     * @param messageText The message text content.
     */
    public void sendChatMessageToAll(Player from, String messageText) {
        PushMessage message = createPushMessageTemplate(LobbyPushMessageDataType.CHAT_MESSAGE);
        message.getData()
                .add("from", from.getName())
                .add("message", messageText);

        LOGGER.log(Level.FINEST, "LOBBY MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     * Sends a chat message.
     *
     * @param from Sender.
     * @param to Recipient.
     * @param messageText Message text content.
     * @param isPrivate Whether the message is a private message to the
     * recipient.
     */
    public void sendChatMessage(Player from, Player to, String messageText, boolean isPrivate) {
        PushMessage message = createPushMessageTemplate(LobbyPushMessageDataType.CHAT_MESSAGE);
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

    /**
     * Sends a player busy event to everyone.
     *
     * @param player The player who is now busy.
     */
    public void sendPlayerBusy(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyPushMessageDataType.STATUS);
        message.getData()
                .add("name", player.getName())
                .add("status", "busy");
        this.sendMessageToAllSessions(message);
    }

    /**
     * Sends a player free event to everyone.
     *
     * @param player The player who is now free.
     */
    public void sendPlayerFree(Player player) {
        PushMessage message = createPushMessageTemplate(LobbyPushMessageDataType.STATUS);
        message.getData()
                .add("name", player.getName())
                .add("status", "free");
        this.sendMessageToAllSessions(message);
    }

    /**
     * Creates a push message template, for message to be sent to players.
     *
     * @param type The lobby message type.
     * @return The message, ready for filling with the rest of the fields.
     */
    private PushMessage createPushMessageTemplate(LobbyPushMessageDataType type) {
        PushMessage message = new PushMessage(MessageContext.LOBBY);
        message.getData()
                .add("type", type.getCode());
        return message;
    }

    /**
     * Sends the given message to all sessions.
     *
     * @param message The message to send.
     */
    private void sendMessageToAllSessions(Message message) {
        String messageString = message.toJsonObject().toString();
        playerManager.getAllSessions().forEach(session -> {
            LOGGER.log(Level.FINEST, "send to session: {0}", session.getId());
            session.getAsyncRemote().sendText(messageString);
        });
    }

}
