/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.lobby;

import fi.misaki.gomoku.protocol.Message;
import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.protocol.PushMessage;
import fi.misaki.gomoku.server.RequestHandler;
import fi.misaki.gomoku.server.auth.User;
import fi.misaki.gomoku.server.auth.UserManager;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;

/**
 *
 * @author vlumi
 */
@Stateless
public class LobbyManager implements Serializable {

    private static final long serialVersionUID = 3409124989853945066L;

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    @Inject
    private UserManager userManager;

    /**
     * Send a join message to all open sessions.
     *
     * @param user The user who joined.
     */
    public void sendJoinMessage(User user) {
        PushMessage message = createPushMessageTemplate(LobbyMessageType.JOIN);
        message.getPayload()
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
        PushMessage message = createPushMessageTemplate(LobbyMessageType.PART);
        message.getPayload()
                .add("name", user.getName());

        LOGGER.log(Level.FINEST, "LOBBY PART MESSAGE: {0}", message);
        sendMessageToAllSessions(message);
    }

    /**
     *
     * @param user
     * @param messageText
     */
    public void sendMessage(User user, String messageText) {
        PushMessage message = createPushMessageTemplate(LobbyMessageType.MESSAGE);
        message.getPayload()
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
    private PushMessage createPushMessageTemplate(LobbyMessageType type) {
        PushMessage message = new PushMessage();
        message.setType(MessageType.LOBBY);
        message.setPayload(Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("type", type.getCode())
        );
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
