/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server;

import fi.misaki.gomoku.protocol.ErrorMessage;
import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.user.UserManager;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.lobby.LobbyManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author vlumi
 */
@ServerEndpoint("/gomoku")
public class GomokuServer {

    private static final Logger LOGGER = Logger.getLogger(GomokuServer.class.getName());

    @Inject
    private UserManager userManager;
    @Inject
    private RequestHandler requestHandler;
    @Inject
    private LobbyManager lobbyManager;

    /**
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        LOGGER.log(Level.FINEST, "[{0}] OPEN", session.getId());
    }

    /**
     *
     * @param message
     * @param session
     * @return
     */
    @OnMessage
    public String onMessage(String message, Session session) {
        LOGGER.log(Level.FINEST, "[{0}] MSG {1}", new String[]{session.getId(), message});

        try {
            this.requestHandler.handleRequest(message, session);

        } catch (Exception ex) {
            LOGGER.log(Level.INFO, null, ex);
            ErrorMessage errorMessage;
            if (ex instanceof InvalidRequestException) {
                errorMessage = new ErrorMessage(ex.getMessage());
            } else {
                errorMessage = new ErrorMessage("Unknown error.");
            }
            return errorMessage.toJsonObject().toString();
        }

        return null;
    }

    /**
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        LOGGER.log(Level.FINEST, "[{0}] CLOSE", session.getId());

        User user = userManager.getUserForSessionId(session.getId());
        userManager.endSession(session);
        if (user != null && user.getSessions().isEmpty()) {
            // No more open sessions for the user.
            lobbyManager.sendPartMessage(user);
        }
    }

}
