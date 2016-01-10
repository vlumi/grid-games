/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.lobby;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestPayloadHandler;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.user.UserManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
@Stateless
public class LobbyRequestPayloadHandler extends RequestPayloadHandler {

    private static final long serialVersionUID = 7826083979194479480L;

    private static final Logger LOGGER = Logger.getLogger(LobbyRequestPayloadHandler.class.getName());

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
    public void handleRequestPayload(Session session, JsonObject payload) throws InvalidRequestException {

        User user = userManager.getUserForSessionId(session.getId());

        switch (LobbyMessagePayloadType.ofCode(payload.getString("type", ""))) {
            case MESSAGE:
                String message = payload.getString("message", "");
                LOGGER.log(Level.FINEST, "LOBBY MESSAGE: {0}", message);

                lobbyManager.sendMessageToAll(user, message);
                break;
            default:
                throw new InvalidRequestException("Invalid lobby request type.");

        }
    }

}
