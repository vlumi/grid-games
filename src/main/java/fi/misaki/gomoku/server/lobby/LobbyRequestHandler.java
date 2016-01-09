/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.lobby;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.protocol.Response;
import fi.misaki.gomoku.server.RequestPayloadHandler;
import fi.misaki.gomoku.server.GomokuServer;
import fi.misaki.gomoku.server.auth.User;
import fi.misaki.gomoku.server.auth.UserManager;
import fi.misaki.gomoku.server.lobby.LobbyMessageType;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
@Stateless
public class LobbyRequestHandler extends RequestPayloadHandler {

    private static final long serialVersionUID = 7826083979194479480L;

    private static final Logger LOGGER = Logger.getLogger(GomokuServer.class.getName());

    @Inject
    private UserManager userManager;
    @Inject
    private LobbyManager lobbyManager;

    /**
     *
     * @param session
     * @param payload
     * @return
     * @throws InvalidRequestException
     */
    @Override
    public JsonObjectBuilder handleRequestPayload(Session session, JsonObject payload) throws InvalidRequestException {

        User user = userManager.getUserForSession(session.getId());

        switch (LobbyMessageType.ofCode(payload.getString("type", ""))) {
            case MESSAGE:
                String message = payload.getString("message", "");
                LOGGER.log(Level.FINEST, "LOBBY MESSAGE: {0}", message);

                lobbyManager.sendMessage(user, message);
                break;
            default:
                throw new InvalidRequestException("Invalid lobby request type.");

        }
        // TODO: parse the input
        // TODO: take action, based on input
        JsonObjectBuilder responsePayload = Json.createBuilderFactory(null)
                .createObjectBuilder();

        return responsePayload;
    }

}
