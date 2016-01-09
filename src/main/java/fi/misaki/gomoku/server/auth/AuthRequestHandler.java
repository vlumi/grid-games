/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.auth;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestHandler;
import fi.misaki.gomoku.server.RequestPayloadHandler;
import fi.misaki.gomoku.server.lobby.LobbyManager;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
@Stateless
public class AuthRequestHandler extends RequestPayloadHandler {

    private static final long serialVersionUID = 8272657796866836750L;

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    @Inject
    private UserManager userManager;
    @Inject
    private LobbyManager lobbyManager;

    @Override
    public JsonObjectBuilder handleRequestPayload(Session session, JsonObject payload) throws InvalidRequestException {
        JsonObjectBuilder responsePayload = loginUser(session, payload);

        User user = userManager.getUserForSession(session.getId());
        if (user.getSessions().size() == 1) {
            // New user
            lobbyManager.sendJoinMessage(user);
        }
        return responsePayload;
    }

    /**
     *
     * @param request
     * @param session
     * @throws InvalidRequestException
     */
    private JsonObjectBuilder loginUser(Session session, JsonObject payload)
            throws InvalidRequestException {

        // TODO: hash the password
        String name = payload.getString("name", "");
        String password = payload.getString("password", "");
        User user = userManager.startSession(name, password, session);

        JsonArrayBuilder membersBuilder = Json.createBuilderFactory(null)
                .createArrayBuilder();
        this.userManager.getMembers().forEach(member -> membersBuilder.add(member));

        return Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("name", user.getName())
                .add("members", membersBuilder);
    }

}
