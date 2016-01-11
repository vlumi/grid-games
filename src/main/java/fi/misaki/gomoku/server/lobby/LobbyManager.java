package fi.misaki.gomoku.server.lobby;

import fi.misaki.gomoku.protocol.Message;
import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.protocol.PushMessage;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.user.UserManager;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

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
     * @param user
     * @param messageText
     */
    public void sendMessageToAll(User user, String messageText) {
        PushMessage message = createPushMessageTemplate(LobbyMessageDataType.MESSAGE);
        message.getData()
                .add("from", user.getName())
                .add("message", messageText);

        LOGGER.log(Level.FINEST, "LOBBY MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
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
