/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestPayloadHandler;
import fi.misaki.gomoku.server.auth.User;
import fi.misaki.gomoku.server.auth.UserManager;
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
public class GomokuRequestHandler extends RequestPayloadHandler {

    private static final long serialVersionUID = 7531558038666358138L;

    @Inject
    private UserManager userManager;

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

        // TODO: parse the input
        // TODO: take action, based on input
        JsonObjectBuilder responsePayload = Json.createBuilderFactory(null)
                .createObjectBuilder();

        return responsePayload;
    }

}
