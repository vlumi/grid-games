package fi.misaki.grid.server;

import fi.misaki.grid.protocol.ErrorMessage;
import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.server.game.GameManager;
import fi.misaki.grid.server.lobby.LobbyManager;
import fi.misaki.grid.server.player.Player;
import fi.misaki.grid.server.player.PlayerManager;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the game server, creating the server WebSocket end point.
 *
 * @author vlumi
 */
@ServerEndpoint("/game")
public class GameServer {

    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    @Inject
    private RequestHandler requestHandler;
    @Inject
    private PlayerManager playerManager;
    @Inject
    private LobbyManager lobbyManager;
    @Inject
    private GameManager gameManager;

    /**
     * Client opening a connection.
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        LOGGER.log(Level.FINEST, "[{0}] OPEN", session.getId());
    }

    /**
     * Client sending a message through an open connection.
     * <p>
     * The message handling is delegated to RequestHandler.
     *
     * @param message The raw message string sent by the client.
     * @param session The current session.
     * @return An error message to be sent to the client, or null.
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
     * Client closing its connection.
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        LOGGER.log(Level.FINEST, "[{0}] CLOSE", session.getId());

        Player player;
        try {
            player = playerManager.getPlayerForSessionId(session.getId());
        } catch (InvalidRequestException ex) {
            LOGGER.log(Level.INFO, "Exception when closing WebSocket: {0}", ex);
            return;
        }
        playerManager.endSession(session);
        if (player != null && player.getSessions().isEmpty()) {
            // No more open sessions for the player.
            gameManager.leavePlayer(player);
            lobbyManager.sendPartMessage(player);
        }
    }

}
