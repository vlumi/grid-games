/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.user;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.protocol.PushMessage;
import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.server.RequestPayloadHandler;
import fi.misaki.gomoku.server.lobby.LobbyManager;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
@Stateless
public class UserRequestPayloadHandler extends RequestPayloadHandler {

    private static final long serialVersionUID = 8272657796866836750L;

    private static final Logger LOGGER = Logger.getLogger(UserRequestPayloadHandler.class.getName());

    @Inject
    private UserManager userManager;
    @Inject
    private LobbyManager lobbyManager;

    /**
     *
     * @param session
     * @param payload
     * @throws InvalidRequestException
     */
    @Override
    public void handleRequestPayload(Session session, JsonObject payload)
            throws InvalidRequestException {
        User user = loginUser(session, payload);
        LOGGER.log(Level.FINE, "User joined: {0}", user.getName());

        userManager.sendPostLoginMessage(user);
        lobbyManager.sendInitMessage(user);

        if (user.getSessions().size() == 1) {
            // New user
            lobbyManager.sendJoinMessage(user);
        }
    }

    /**
     *
     * @param request
     * @param session
     * @throws InvalidRequestException
     */
    private User loginUser(Session session, JsonObject payload)
            throws InvalidRequestException {

        // TODO: hash the password
        String name = payload.getString("name", "");
        String password = payload.getString("password", "");
        return userManager.startSession(name, password, session);
    }

}
