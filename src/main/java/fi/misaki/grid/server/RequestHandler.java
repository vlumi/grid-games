package fi.misaki.grid.server;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.protocol.RequestMessage;
import fi.misaki.grid.server.game.GameRequestDataHandler;
import fi.misaki.grid.server.lobby.LobbyRequestDataHandler;
import fi.misaki.grid.server.player.PlayerRequestDataHandler;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.Session;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Handler for the top-level message envelope, passing the handling to the
 * associated message data handler by the message type.
 *
 * @author vlumi
 */
@Stateless
public class RequestHandler implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());
    private static final long serialVersionUID = 1241174184175430192L;

    @Inject
    private PlayerRequestDataHandler authRequestHandler;
    @Inject
    private LobbyRequestDataHandler lobbyRequestHandler;
    @Inject
    private GameRequestDataHandler gameRequestHandler;

    /**
     * Handle the request message received from the client, passing the data
     * handling to the associated handler.
     *
     * @param message The received message
     * @param session Current session.
     * @throws fi.misaki.grid.protocol.InvalidRequestException
     */
    public void handleRequest(String message, Session session)
            throws InvalidRequestException {

        RequestMessage request = new RequestMessage(message);

        RequestDataHandler handler;
        switch (request.getContext()) {
            case PLAYER:
                handler = this.authRequestHandler;
                break;
            case LOBBY:
                handler = this.lobbyRequestHandler;
                break;
            case GAME:
                handler = this.gameRequestHandler;
                break;
            default:
                throw new InvalidRequestException("Invalid request type.");
        }
        handler.handleRequestData(session, request.getData());
    }

}
