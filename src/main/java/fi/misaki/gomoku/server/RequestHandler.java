/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server;

import fi.misaki.gomoku.server.auth.AuthRequestHandler;
import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.protocol.Request;
import fi.misaki.gomoku.protocol.Response;
import fi.misaki.gomoku.server.gomoku.GomokuRequestHandler;
import fi.misaki.gomoku.server.lobby.LobbyRequestHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonException;
import javax.websocket.Session;

/**
 * Handler for the top-level message envelope, passing the handling to the
 * associated message payload handler by the message type.
 *
 * @author vlumi
 */
@Stateless
public class RequestHandler {

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    @Inject
    private AuthRequestHandler authRequestHandler;
    @Inject
    private LobbyRequestHandler lobbyRequestHandler;
    @Inject
    private GomokuRequestHandler gomokuRequestHandler;

    /**
     *
     * @param message
     * @param session
     * @return
     */
    public Response handleRequest(String message, Session session) {

        Response response = new Response();
        try {
            Request request = new Request(message);
            response.setType(request.getType());

            RequestPayloadHandler handler;
            switch (request.getType()) {
                case AUTH:
                    handler = this.authRequestHandler;
                    break;
                case LOBBY:
                    handler = this.lobbyRequestHandler;
                    break;
                case GOMOKU:
                    handler = this.gomokuRequestHandler;
                    break;
                default:
                    throw new InvalidRequestException("Invalid request type.");
            }
            response.setPayload(handler.handleRequestPayload(session, request.getPayload()));

        } catch (Exception ex) {
            LOGGER.log(Level.INFO, null, ex);
            response.setError(true);
            if (ex instanceof InvalidRequestException) {
                response.setMessage(ex.getMessage());
            } else if (ex instanceof JsonException) {
                response.setMessage("Invalid request.");
            } else {
                response.setMessage("Unknown error.");
            }
        }

        return response;
    }

}
