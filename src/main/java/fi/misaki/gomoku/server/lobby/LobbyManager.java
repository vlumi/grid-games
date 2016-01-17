package fi.misaki.gomoku.server.lobby;

import fi.misaki.gomoku.protocol.Message;
import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.protocol.PushMessage;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.user.UserManager;
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
    private UserManager userManager;

    public void handleChatMessageRequest(User user, JsonObject data) {
        String message = data.getString("message", "");
        String to = data.getString("to", "");

        LOGGER.log(Level.FINEST, "Lobby message to {0}: {1}", new String[]{to, message});

        if (to.isEmpty()) {
            sendChatMessageToAll(user, message);
        } else {
            boolean isPrivate = data.getBoolean("private", false);
            User toUser = userManager.getUserForName(to);
            sendChatMessage(user, toUser, message, isPrivate);
        }
    }

    /**
     *
     * @param user
     */
    public void sendInitMessage(User user) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.INIT);
        message.getData()
                .add("members", this.userManager.getMembersAsJsonArrayBuilder());
        userManager.sendMessageToUser(user, message);
    }

    /**
     * Send a join message to all open sessions.
     *
     * @param user The user who joined.
     */
    public void sendJoinMessage(User user) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.JOIN);
        message.getData()
                .add("name", user.getName());

        LOGGER.log(Level.FINEST, "LOBBY JOIN MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     * Send a part message to all open sessions.
     *
     * @param user The user who left.
     */
    public void sendPartMessage(User user) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.PART);
        message.getData()
                .add("name", user.getName());

        LOGGER.log(Level.FINEST, "LOBBY PART MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     *
     * @param from
     * @param messageText
     */
    public void sendChatMessageToAll(User from, String messageText) {
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
    public void sendChatMessage(User from, User to, String messageText, boolean isPrivate) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.CHAT_MESSAGE);
        message.getData()
                .add("from", from.getName())
                .add("to", to.getName())
                .add("message", messageText)
                .add("private", isPrivate);

        LOGGER.log(Level.FINEST, "LOBBY MESSAGE: {0}", message);
        if (isPrivate) {
            final Set<User> targetUsers = Stream.of(from, to).collect(Collectors.toSet());
            this.userManager.sendMessageToUsers(targetUsers, message);
        } else {
            sendMessageToAllSessions(message);
        }
    }

    public void sendUserBusy(User user) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.STATUS);
        message.getData()
                .add("status", "busy");

    }

    public void sendUserFree(User user) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.STATUS);
        message.getData()
                .add("status", "free");
    }

    /**
     *
     * @param type
     * @return
     */
    private PushMessage createPushMessageTemplate(LobbyMessageDataType type) {
        PushMessage message = new PushMessage(MessageType.LOBBY);
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
        userManager.getAllSessions().forEach(session -> {
            LOGGER.log(Level.FINEST, "send to session: {0}", session.getId());
            session.getAsyncRemote().sendText(messageString);
        });
    }

}
