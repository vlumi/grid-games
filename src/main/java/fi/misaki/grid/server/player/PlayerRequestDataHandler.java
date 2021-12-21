package fi.misaki.grid.server.player;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.server.RequestDataHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.websocket.Session;
import java.util.logging.Logger;

/**
 * Request data handler for player messages.
 *
 * @author vlumi
 */
@ApplicationScoped
public class PlayerRequestDataHandler extends RequestDataHandler {

    private static final long serialVersionUID = 8272657796866836750L;

    private static final Logger LOGGER = Logger.getLogger(PlayerRequestDataHandler.class.getName());

    @Inject
    private PlayerManager playerManager;

    /**
     * Handle the request data of the received message.
     *
     * @param session The WebSocket session where the message was received from.
     * @param data    The data content of the received message.
     * @throws InvalidRequestException in case of any errors
     */
    @Override
    public void handleRequestData(Session session, JsonObject data)
            throws InvalidRequestException {

        switch (PlayerRequestMessageDataType.ofCode(data.getString("type", ""))) {
            case LOGIN:
                playerManager.handleLoginRequest(session, data);
                break;
            default:
                throw new InvalidRequestException("Invalid player request type.");
        }

    }

}
