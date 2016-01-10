/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestPayloadHandler;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.user.UserManager;
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
public class GomokuRequestPayloadHandler extends RequestPayloadHandler {

    private static final long serialVersionUID = 7531558038666358138L;

    private static final Logger LOGGER = Logger.getLogger(GomokuRequestPayloadHandler.class.getName());

    @Inject
    private UserManager userManager;

    /**
     *
     * @param session
     * @param payload
     * @throws InvalidRequestException
     */
    @Override
    public void handleRequestPayload(Session session, JsonObject payload) throws InvalidRequestException {

        User user = userManager.getUserForSessionId(session.getId());

        // TODO: parse the input
        // TODO: take action, based on input
        JsonObjectBuilder responsePayload = Json.createBuilderFactory(null)
                .createObjectBuilder();

    }

}
