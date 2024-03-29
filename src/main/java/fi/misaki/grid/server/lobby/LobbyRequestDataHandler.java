package fi.misaki.grid.server.lobby;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.server.RequestDataHandler;
import fi.misaki.grid.server.player.Player;
import fi.misaki.grid.server.player.PlayerManager;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.websocket.Session;
import java.util.logging.Logger;

/**
 * Request data handler for lobby messages.
 *
 * @author vlumi
 */
@ApplicationScoped
public class LobbyRequestDataHandler extends RequestDataHandler {

    private static final long serialVersionUID = 7826083979194479480L;

    private static final Logger LOGGER = Logger.getLogger(LobbyRequestDataHandler.class.getName());

    @Inject
    private PlayerManager playerManager;
    @Inject
    private LobbyManager lobbyManager;

    /**
     * Handle the request data of the received message.
     *
     * @param session The WebSocket session where the message was received from.
     * @param data    The data content of the received message.
     * @throws InvalidRequestException in case of any errors
     */
    @Override
    public void handleRequestData(Session session, JsonObject data) throws InvalidRequestException {

        Player player = playerManager.getPlayerForSessionId(session.getId());

        switch (LobbyRequestMessageDataType.ofCode(data.getString("type", ""))) {
            case CHAT_MESSAGE:
                lobbyManager.handleChatMessageRequest(player, data);
                break;
            default:
                throw new InvalidRequestException("Invalid lobby request type.");
        }
    }

}
